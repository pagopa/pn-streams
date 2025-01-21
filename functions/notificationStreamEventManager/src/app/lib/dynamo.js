const { DynamoDBClient, PutItemCommand } = require("@aws-sdk/client-dynamodb");
const { unmarshall } = require("@aws-sdk/util-dynamodb");

const dynamoDbClient = new DynamoDBClient({ region: process.env.REGION });

function buildNotificationItem(newImage, tableName, ttlOffset) {
  const parsedData = unmarshall(newImage);
  const now = Math.floor(Date.now() / 1000);
  const ttl = Math.max(now + ttlOffset, 0);

  // Validate `iun` and `group` during item creation
  if (!parsedData.iun) {
    throw new Error(`Missing required fields 'iun' in data: ${JSON.stringify(parsedData)}`);
  }

  return {
    TableName: tableName,
    Item: {
      hashKey: { S: parsedData.iun },
      group: { S: parsedData.group }, // Storing `group` as a single string
      creationDate: { S: parsedData.sentAt },
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