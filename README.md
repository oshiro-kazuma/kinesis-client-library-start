kinesis-client-library-start
------------------------------------

Kinesis Client Library を使ってstreamの内容を標準出力する

```bash
# stream名をきめておく
stream_name="kinesis-test-stream"

# streamを作成する
aws kinesis create-stream --stream-name $stream_name --shard-count 1

# StreamStatusがCREATINGからACTIVEになるのを確認する。
aws kinesis describe-stream --stream-name $stream_name

# kinesis consumer 起動
sbt run -DaccessKeyId=XXX -DsecretAccessKey=XXX

# データ投入
aws kinesis put-record --stream-name $stream_name --data hogehoge_$RANDOM --partition-key $RANDOM
```
