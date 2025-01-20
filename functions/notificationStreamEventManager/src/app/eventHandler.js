const { extractKinesisData } = require("../app/lib/kinesis");
const { putNotificationItem, buildNotificationItem } = require("../app/lib/dynamo");

exports.handleEvent = async (event) => {

  const cdcEvents = extractKinesisData(event);
  console.log(`Batch size: ${cdcEvents.length} cdc`);

  if (cdcEvents.length === 0) {
    console.log("No events to process");
    return { batchItemFailures: [] };
  }

  let batchItemFailures = [];

  for (const cdcEvent of cdcEvents) {
    try {
      const notificationItem = buildNotificationItem(cdcEvent.dynamodb.NewImage);

      // Validate `iun` and `group` values
      if (!notificationItem.Item.hashKey.S || !notificationItem.Item.group.S) {
        const errorMessage = `Invalid data: missing or empty 'iun' or 'group' for event: ${JSON.stringify(cdcEvent)}`;
        console.error(errorMessage);
        throw new Error(errorMessage);
      }

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
