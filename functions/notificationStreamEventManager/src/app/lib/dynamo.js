const { DynamoDBClient, PutItemCommand } = require("@aws-sdk/client-dynamodb");
const { unmarshall } = require("@aws-sdk/util-dynamodb");

const dynamoDbClient = new DynamoDBClient({ region: process.env.REGION });
const TABLE_NAME = process.env.TABLE_NAME;

function buildNotificationItem(newImage) {
  const parsedData = unmarshall(newImage);
  const now = Math.floor(Date.now() / 1000);
  const ttlOffset = parseInt(process.env.TTL_OFFSET, 10);
  const ttl = Math.max(now + ttlOffset, 0);

  // Validate `iun` and `group` during item creation
  if (!parsedData.iun || !parsedData.group) {
    throw new Error(`Missing required fields 'iun' or 'group' in data: ${JSON.stringify(parsedData)}`);
  }

  return {
    TableName: TABLE_NAME,
    Item: {
      hashKey: { S: parsedData.iun },
      group: { S: parsedData.group }, // Storing `group` as a single string
      creationDate: { S: new Date().toISOString() },
      ttl: { N: ttl.toString() },
    },
  };
}

async function putNotificationItem(item) {
  const command = new PutItemCommand(item);
  try {
    await dynamoDbClient.send(command);
  } catch (error) {
    console.error("Error putting item to DynamoDB:", error);
    throw error;
  }
}

module.exports = { putNotificationItem, buildNotificationItem };