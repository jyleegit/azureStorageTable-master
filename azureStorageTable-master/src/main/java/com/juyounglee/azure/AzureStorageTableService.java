package com.juyounglee.azure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.Operators;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import com.microsoft.azure.storage.table.TableResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AzureStorageTableService {
	
	String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=juyounglee;AccountKey=8pHAqDuBGHfkuED6nGl61br9OBCo1t4eszWvd7vkxwrzl8jW/MVXQfI3wa0pEvtEsa5InhatzPr7mwVWEC9w0w==;EndpointSuffix=core.windows.net";
	
	
	public boolean createTable(String tableName)
	{
		try
		{
		    // Retrieve storage account from connection-string.
		    CloudStorageAccount storageAccount =
		        CloudStorageAccount.parse(storageConnectionString);

		    // Create the table client.
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();

		    CloudTable cloudTable = tableClient.getTableReference(tableName);
		    return cloudTable.createIfNotExists();
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
		return false;
	}

	public List<String> getTableList() {

	    List<String> resList = new ArrayList<>();
		try
		{
		    // Retrieve storage account from connection-string.
		    CloudStorageAccount storageAccount =
		        CloudStorageAccount.parse(storageConnectionString);

		    // Create the table client.
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();

		    
		    // Loop through the collection of table names.
		    for (String table : tableClient.listTables())
		    {
		        // Output each table name.
		        System.out.println(table);
		        resList.add(table);
		    }
		    
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
		
		return resList;
	}
	
	public EpaVO getEpa(String partitionKey, String rowKey) {

		try
		{
		    // Retrieve storage account from connection-string.
		    CloudStorageAccount storageAccount =
		        CloudStorageAccount.parse(storageConnectionString);

		    // Create the table client.
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();

		    // Create a cloud table object for the table.
		    CloudTable cloudTable = tableClient.getTableReference("epa");
		    
		    TableOperation retrieveOperation = TableOperation.retrieve(partitionKey, rowKey, EpaVO.class);
		    
		    
		    // Submit the operation to the table service.
		    TableResult res = cloudTable.execute(retrieveOperation);
		    
		    EpaVO epa = res.getResultAsType();
		    
		    log.info("epa: {}", epa);
		    
		    
		    
		    return epa;
		    
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
		return null;
	
	}
	
	public ArrayList<EpaVO> searchEpa(String fac_id, int chamber) {

		try
		{
		    // Retrieve storage account from connection-string.
		    CloudStorageAccount storageAccount =
		        CloudStorageAccount.parse(storageConnectionString);

		    // Create the table client.
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();

		    // Create a cloud table object for the table.
		    CloudTable cloudTable = tableClient.getTableReference("epa");
		    
		    String fac_idFilter = TableQuery.generateFilterCondition(
		    		"Fac_id",
		            QueryComparisons.EQUAL,
		            fac_id);
		    
		    String chamberFilter = TableQuery.generateFilterCondition(
		            "Chamber",
		            QueryComparisons.LESS_THAN,
		            chamber);
		    
		    String combinedFilter = TableQuery.combineFilters(fac_idFilter,
		            Operators.AND, chamberFilter);
		    
		    log.info("combinedFilter: " + combinedFilter);
		    
		    TableQuery<EpaVO> rangeQuery =
		            TableQuery.from(EpaVO.class)
		            .where(combinedFilter);
		    
		    Iterable<EpaVO> iter = cloudTable.execute(rangeQuery);
		    
		    ArrayList<EpaVO> list = Lists.newArrayList(iter);
	        // Loop through the results, displaying information about the entity
	        for (EpaVO entity : list) {
	            log.info("fac_id: {}, eqp_id: {}, area: {}, chamber: {}", entity.getFac_id(), entity.getEqp_id(), entity.getArea(), entity.getChamber());
	        }
		    
		    
		    
		    return list;
		    
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
		return null;
	
	}
	
	
	public String insertEpa(EpaVO p)
	{
		System.out.println(p);
		try
		{
		    // Retrieve storage account from connection-string.
		    CloudStorageAccount storageAccount =
		        CloudStorageAccount.parse(storageConnectionString);

		    // Create the table client.
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();

		    // Create a cloud table object for the table.
		    CloudTable cloudTable = tableClient.getTableReference("epa");
		    // unique 한 값조합이 되도록 아래 2개 세팅
		    p.setPartitionKey(p.getId());
		    p.setRowKey(p.getId());

		    TableOperation insertCustomer1 = TableOperation.insertOrReplace(p);		    

		    // Submit the operation to the table service.
		    TableResult res = cloudTable.execute(insertCustomer1);		    
		    
		    System.out.println(res.getHttpStatusCode());
		    
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
		return p.getId() + "/" + p.getId();
	}
}
