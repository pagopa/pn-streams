const { extractKinesisData } = require("../app/lib/kinesis");
const { putNotificationItem, buildNotificationItem } = require("../app/lib/dynamo");

exports.handleEvent = async (event) => {

  const TABLE_NAME = process.env.TABLE_NAME;
  const TTL_OFFSET = process.env.TTL_OFFSET;

  const cdcEvents = extractKinesisData(event);
  console.log(`Batch size: ${cdcEvents.length} cdc`);

  if (cdcEvents.length === 0) {
    console.log("No events to process");
    return { batchItemFailures: [] };
  }

  let batchItemFailures = [];

  for (const cdcEvent of cdcEvents) {
    try {
      const notificationItem = buildNotificationItem(cdcEvent.dynamodb.NewImage, TABLE_NAME, parseInt(TTL_OFFSET, 10));

      await putNotificationItem(notificationItem);
    } catch (error) {
      console.error("Error processing event", cdcEvent, error);
      batchItemFailures.push({ itemIdentifier: cdcEvent.kinesisSeqNumber });
    }
  }

  if (batchItemFailures.length > 0) {
    console.log("Process finished with errors!");
  }

  return { batchItemFailures };
};
