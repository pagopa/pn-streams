const crypto = require('crypto');

exports.mapEvents = (events) => {
  let result = [];

  for (let i = 0; i < events.length; i++) {

    let timelineEvent = events[i];

    let date = new Date();

    timelineEvent.timelineObject.details = JSON.stringify(timelineEvent.timelineObject.details)

    let action = {
      timelineElementInternal: timelineEvent.timelineObject,
      eventId: `${date.toISOString()}_${timelineEvent.timelineObject.timelineElementId}`,
      type: 'REGISTER_EVENT'
    };

    let messageAttributes = {
      publisher: {
        DataType: 'String',
        StringValue: 'deliveryPush'
      },
      iun: {
        DataType: 'String',
        StringValue: timelineEvent.timelineObject.iun
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


