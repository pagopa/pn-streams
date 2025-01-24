const { unmarshall } = require("@aws-sdk/util-dynamodb");
const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");
const {
  PutCommand,
  DynamoDBDocumentClient
} = require("@aws-sdk/lib-dynamodb");

const client = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(client);

function buildNotificationItem(newImage, tableName, ttlOffset) {
  const parsedData = unmarshall(newImage);
  const now = Math.floor(Date.now() / 1000);
  const ttl = Math.max(now + ttlOffset, 0);

  // Validate `iun` during item creation
  if (!parsedData.iun) {
    throw new Error(`Missing required fields 'iun' in data: ${JSON.stringify(parsedData)}`);
  }

  return {
    TableName: tableName,
    Item: {
      hashKey: { S: parsedData.iun },
      group: { S: parsedData.group ? parsedData.group : null},
      creationDate: { S: parsedData.sentAt },
      ttl: { N: ttl.toString() },
    },
  };
}

async function putNotificationItem(item) {
  const command = new PutCommand({
    TableName: process.env.TABLE_NAME,
    Item: item,
  });

  try {
    await docClient.send(command);
  } catch (error) {
    console.error("Error putting item to DynamoDB:", error);
    throw error;
  }
}

module.exports = { putNotificationItem, buildNotificationItem };