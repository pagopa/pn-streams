const { unmarshall } = require("@aws-sdk/util-dynamodb");
const crypto = require('crypto');

exports.mapEvents = (events) => {
  let result = [];

  for (let i = 0; i < events.length; i++) {

    let timelineObj = unmarshall(events[i].dynamodb.NewImage);

    let date = new Date();

    let action = {
      event: timelineObj,
      eventId: `${date.toISOString()}_${timelineObj.timelineElementId}`,
      type: 'REGISTER_EVENT'
    };

    let messageAttributes = {
      publisher: {
        DataType: 'String',
        StringValue: 'deliveryPush'
      },
      iun: {
        DataType: 'String',
        StringValue: timelineObj.iun
      },
      eventId: {
        DataType: 'String',
        StringValue: crypto.randomUUID()
      },
      createdAt: {
        DataType: 'String',
        StringValue: date.toISOString()
      }, 
      eventType:  {
        DataType: 'String',
        StringValue:'WEBHOOK_ACTION_GENERIC'
      },
    };

    let resultElement = {
      Id: events[i].kinesisSeqNumber,
      MessageAttributes: messageAttributes,
      MessageBody: JSON.stringify(action)
    };

    result.push(resultElement);

  }
  return result;
};


