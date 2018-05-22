import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions._


object ghost {
	private val USER_LOST = 0
	private val USER_WIN = 2
	private val PC_WIN = 1

	private val APP_NAME = "Cocktail Ghost"
	private val MASTER_NODE = "local[*]"
	private val LOG_LEVEL = "WARN"

	def main(args: Array[String]): Unit = {

		//Spark Context
		val conf = new SparkConf()
				.setAppName(APP_NAME)
				.setMaster(MASTER_NODE)

		val sc: SparkContext = new SparkContext(conf)
		sc.setLogLevel(LOG_LEVEL)

		// Spark Session
		val spark = SparkSession.builder()
				.appName(APP_NAME)
				.master(MASTER_NODE)
				.config("spark.sql.parquet.compression.codec", "snappy")
				.getOrCreate()
		// Read file source
		val filename = "resources/ghost.csv"
		val drinks: DataFrame = spark.read
				.format("com.databricks.spark.csv")
				.option("sep", ",")
				.option("header", "false")
				.option("inferSchema", "true")
				.load(filename)
				.select(col("_c0").as("name"))
				.withColumn("name", lower(col("name")))

		val word = StringBuilder.newBuilder
		var flag = -2 // -2 first loop, -1 playing, 0 user fail, 1 pc win, 2 user win

		println("===================================")
		println("Tell me the first letter: ")
		println("===================================")

		while (flag < 0) {
			// Check if is the first loop
			if (flag == -1) {
				println("===================================")
				println(s"""The word playing is: "${word.mkString}", insert other letter: """)
				println("===================================")
			} else flag = -1

			word.append(scala.io.StdIn.readChar()) // Read char from terminal
			val maybe = drinks.filter(col("name").startsWith(word.mkString)) // Possibilities

			maybe.count() match {
				case 0 => flag = USER_LOST
				case n =>
					val pair = maybe.filter(_.getString(0).length % 2 == 0)
					val odd = maybe.filter(_.getString(0).length % 2 != 0)
					val first_value = maybe.collect()(0)(0).toString // in case of only one option

					// last char
					if (n == 1 & word.length == first_value.length - 1) {
						word.append(first_value.charAt(word.length))
						flag = PC_WIN
					} else if (word.length == first_value.length) {
						flag = USER_WIN
					} else {
						// try to select one pair word
						if (pair.count() > 0) {
							word.append(pair.collect()(scala.util.Random.nextInt(pair.count().toInt))
									.mkString.charAt(word.length))
						} else {
							word.append(get_longest_word(odd, spark).charAt(word.length))
						}
					}
			}

		}

		flag match {
			case USER_LOST =>
				println("==============================================================")
				println("OHH, YOU LOSE, THERE ARE NOT ANY SPIRIT DRINK WITH THAT NAME")
				println("==============================================================")
			case USER_WIN =>
				println("==============================================================")
				println(s"YOU WIN!!! THE WINNER WORD WAS ${word.mkString}")
				println("==============================================================")
			case PC_WIN =>
				println("==============================================================")
				println("SORRY, NOBODY CAN WIN AGAINST ME")
				println("==============================================================")
		}


	}

	private def get_longest_word(df: DataFrame, spark: SparkSession): String = {
		val max_word = df.select(max(length(col("name")))).collect()(0)(0)
		df.filter(length(col("name")).equalTo(max_word)).collect()(0)(0).toString
	}
}


