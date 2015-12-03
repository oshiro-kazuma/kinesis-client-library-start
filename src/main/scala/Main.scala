import java.net.InetAddress
import java.util
import java.util.UUID
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentialsProvider}
import com.amazonaws.services.kinesis.clientlibrary.interfaces.{IRecordProcessorCheckpointer, IRecordProcessor, IRecordProcessorFactory}
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.{Worker, KinesisClientLibConfiguration}
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason
import com.amazonaws.services.kinesis.model.Record

object Main {
  val accessKeyId = System.getProperty("accessKeyId")
  val secretAccessKey = System.getProperty("secretAccessKey")

  val appName = "kinesis-test-app"
  val streamName = "kinesis-test-stream"

  val initialPosition = "LATEST"
  val region = "ap-northeast-1"
  val idleTimeBetweenReadsInMillis = 3000

  var worker: Worker = null

  def main(args: Array[String]): Unit = {
    val workerId = InetAddress.getLocalHost.getCanonicalHostName + ":" + UUID.randomUUID
    val credentialsProvider: AWSCredentialsProvider = new StaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey))

    val kclConf = new KinesisClientLibConfiguration(appName, streamName, credentialsProvider, workerId)
      .withInitialPositionInStream(InitialPositionInStream.valueOf(initialPosition))
      .withRegionName(region)
      .withIdleTimeBetweenReadsInMillis(idleTimeBetweenReadsInMillis)

    worker = new Worker(StreamTailProcessor.processorFactory, kclConf)
    sys.addShutdownHook {
      worker.shutdown()
      println("worker shutdown.")
      Thread.sleep(3000)
    }
    println(s"worker start. name:$appName stream:$streamName workerId:$workerId")
    worker.run()

  }
}

class StreamTailProcessor extends IRecordProcessor{
  override def shutdown(checkpointer: IRecordProcessorCheckpointer, reason: ShutdownReason): Unit = {
    println(s"Shutting down record processor")
  }

  override def initialize(shardId: String): Unit = {
    println(s"Initialising record processor for shard: $shardId")
  }

  override def processRecords(records: util.List[Record], checkpointer: IRecordProcessorCheckpointer): Unit = {
    import scala.collection.JavaConversions._
    records foreach { r =>
      val line = new String(r.getData.array)
      println(s"[stream-tail] $line")
    }
  }
}

object StreamTailProcessor {
  def processorFactory = new IRecordProcessorFactory {
    def createProcessor(): IRecordProcessor = new StreamTailProcessor
  }
}
