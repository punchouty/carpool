package com.racloop;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class AwsUtil {

	private static String accessKey = "AKIAJ74MXKGZZPYXKEWA";
	private static String secretKey = "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";
	private static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	private static DynamoDB dynamoDB = null;
	private static String journeyPairTable = "JourneyPair";
	private static String journeyTable = "Journey";
	private static String userTable = "RacloopUser";
	private static String ratingTable = "UserRating";

	public static void main(String[] args) throws InterruptedException, ParseException {
		createTables();
		
	}
	
	public static void createTables() throws InterruptedException {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
		client.setEndpoint("http://localhost:8000");
		client.setSignerRegionOverride("local");
		//client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
		dynamoDB = new DynamoDB(client);	
		deleteAllTables();
		createRacloopUseTable();
		createJourneyPairTable();
		createRatingTable();
		createJourneyTable();
	}

	public static void deleteAllTables() throws InterruptedException {
		TableCollection<ListTablesResult> tables = dynamoDB.listTables();
		Iterator<Table> iterator = tables.iterator();
		while (iterator.hasNext()) {
			Table table = iterator.next();
			String tableName = table.getTableName();
			System.out.println(tableName);
			System.out.println("Started deleting table : " + tableName);
			table.delete();
			table.waitForDelete();
			System.out.println("Table deleted : " + tableName);
		}
	}

	public static void createRacloopUseTable() throws InterruptedException {
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Mobile").withAttributeType("S"));
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Email").withAttributeType("S"));
		
		ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
		keySchema.add(new KeySchemaElement().withAttributeName("Mobile").withKeyType(KeyType.HASH));
		
		ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
		indexKeySchema.add(new KeySchemaElement().withAttributeName("Email").withKeyType(KeyType.HASH));
		
		GlobalSecondaryIndex emailIndex = new GlobalSecondaryIndex()
			.withIndexName("Email-index")
			.withKeySchema(indexKeySchema)
			.withProjection(new Projection().withProjectionType(ProjectionType.ALL))
			.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(2L).withWriteCapacityUnits(2L));
		
		CreateTableRequest request = new CreateTableRequest()
				.withTableName(userTable)
				.withKeySchema(keySchema)
				.withGlobalSecondaryIndexes(emailIndex)
				.withAttributeDefinitions(attributeDefinitions)
				.withProvisionedThroughput(
						new ProvisionedThroughput().withReadCapacityUnits(2L)
								.withWriteCapacityUnits(2L));
		System.out.println("Started creating table : " + userTable);
		Table table = dynamoDB.createTable(request);
		table.waitForActive();
		System.out.println("Created successfully : " + userTable);
	}

	public static void createJourneyTable() throws InterruptedException {
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType("S"));
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Mobile").withAttributeType("S"));
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("DateOfJourney").withAttributeType("S"));
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("DateKey").withAttributeType("S"));

		ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
		keySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH));
		
		ArrayList<KeySchemaElement> indexKeySchema1 = new ArrayList<KeySchemaElement>();
		indexKeySchema1.add(new KeySchemaElement().withAttributeName("DateKey").withKeyType(KeyType.HASH));
		//indexKeySchema1.add(new KeySchemaElement().withAttributeName("Mobile").withKeyType(KeyType.RANGE));
		
		ArrayList<KeySchemaElement> indexKeySchema2 = new ArrayList<KeySchemaElement>();
		indexKeySchema2.add(new KeySchemaElement().withAttributeName("Mobile").withKeyType(KeyType.HASH));
		indexKeySchema2.add(new KeySchemaElement().withAttributeName("DateOfJourney").withKeyType(KeyType.RANGE));
		
		GlobalSecondaryIndex index1 = new GlobalSecondaryIndex()
			.withIndexName("DateKey-index")
			.withKeySchema(indexKeySchema1)
			.withProjection(new Projection().withProjectionType(ProjectionType.ALL))
			.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
		
		GlobalSecondaryIndex index2 = new GlobalSecondaryIndex()
			.withIndexName("Mobile-DateOfJourney-index")
			.withKeySchema(indexKeySchema2)
			.withProjection(new Projection().withProjectionType(ProjectionType.ALL))
			.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		
		ArrayList<GlobalSecondaryIndex> indexes = new ArrayList<GlobalSecondaryIndex>();
		indexes.add(index1);
		indexes.add(index2);
		
		CreateTableRequest request = new CreateTableRequest()
				.withTableName(journeyTable)
				.withKeySchema(keySchema)
				.withGlobalSecondaryIndexes(indexes)
				.withAttributeDefinitions(attributeDefinitions)
				.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		System.out.println("Started creating table : " + journeyTable);
		Table table = dynamoDB.createTable(request);
		table.waitForActive();
		System.out.println("Created successfully : " + journeyTable);
	}

	public static void createJourneyPairTable() throws InterruptedException {
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType("S"));
		ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
		keySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH));
		CreateTableRequest request = new CreateTableRequest()
				.withTableName(journeyPairTable)
				.withKeySchema(keySchema)
				.withAttributeDefinitions(attributeDefinitions)
				.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		System.out.println("Started creating table : " + journeyPairTable);
		Table table = dynamoDB.createTable(request);
		table.waitForActive();
		System.out.println("Created successfully : " + journeyPairTable);
	}

	public static void createRatingTable() throws InterruptedException {
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Reviewee").withAttributeType("S"));
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Reviewer").withAttributeType("S"));
		ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
		keySchema.add(new KeySchemaElement().withAttributeName("Reviewee").withKeyType(KeyType.HASH));
		keySchema.add(new KeySchemaElement().withAttributeName("Reviewer").withKeyType(KeyType.RANGE));
		
		CreateTableRequest request = new CreateTableRequest()
				.withTableName(ratingTable)
				.withKeySchema(keySchema)
				.withAttributeDefinitions(attributeDefinitions)
				.withProvisionedThroughput(
						new ProvisionedThroughput().withReadCapacityUnits(2L)
								.withWriteCapacityUnits(2L));
		System.out.println("Started creating table : " + ratingTable);
		Table table = dynamoDB.createTable(request);
		table.waitForActive();
		System.out.println("Created successfully : " + ratingTable);
	}

}
