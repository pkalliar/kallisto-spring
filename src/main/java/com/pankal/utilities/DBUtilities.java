package com.pankal.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

//import org.h2.jdbcx.JdbcDataSource;
// import org.joda.time.DateTime;
// import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import org.postgresql.util.PGobject;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;

/**
 * Created by pankal on 4/21/14.
 */
public class DBUtilities {

    static Connection connection = null;
    static JsonNodeFactory fac = JsonNodeFactory.instance;
    static Logger logger = Logger.getLogger(DBUtilities.class.getName());
    static ObjectMapper mapper = new ObjectMapper();

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    static DateTimeFormatter shdeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    static DatabaseMetaData databaseMetaData = null;
    static Properties props;
    static PGPoolingDataSource source;

    public static boolean connect(String url, String username, String userpwd, String dbname) throws ClassNotFoundException, SQLException, IOException {
//        Class.forName("org.h2.Driver");
//        Connection conn = DriverManager.getConnection(url, username, userpwd);

        if(url.startsWith("jdbc:h2")){
//            JdbcDataSource ds = new JdbcDataSource();
//
//            ds.setURL(url);
//            ds.setUser(username);
//            ds.setPassword(userpwd);
//
//            connection = ds.getConnection();
        }
        else if(url.startsWith("jdbc:postgresql")){
             props = new Properties();
             props.setProperty("user",username);
             props.setProperty("password",userpwd);
             props.setProperty("ssl","true");
             connection = DriverManager.getConnection(url, props);            
            
            databaseMetaData = connection.getMetaData();
        }

        System.out.println("Is connection valid?: " + connection.isValid(3));
        // add application code here
        return connection.isValid(3);
    }
    
    public static boolean connect(String serverName, int port, String username, String userpwd, String dbname) throws ClassNotFoundException, SQLException, IOException {

        source = new PGPoolingDataSource();
        source.setDataSourceName("A Data Source");
        source.setServerName(serverName);
        source.setPortNumber(port);
        source.setDatabaseName(dbname);
        source.setUser(username);
        source.setPassword(userpwd);
        source.setMaxConnections(10);
        connection = source.getConnection();
        
        
        databaseMetaData = connection.getMetaData();
		return connection.isValid(3);        	
    }

	public static boolean connect(DataSource ds){
		try {
			connection = ds.getConnection();
			databaseMetaData = connection.getMetaData();

			return connection.isValid(3);
		} catch (SQLException e) {
			logger.info("Could not connect");
			return false;
		}
	}
    
    private static void reconnect(){
        try {
			connection = source.getConnection();
			databaseMetaData = connection.getMetaData();
		} catch (SQLException e) {
			logger.info("Could not reconnect");
		}     
        

    }

    public static void execute(String sql, String json) throws SQLException{

        System.out.println("Executing sql:" + sql);

        Statement stat = connection.createStatement();
        stat.execute(sql);
        stat.close();

//        connection.commit();
    }


    public static JsonNode getTableNumRecords(String tablename, String criteria){

//        String selectSQL = "SELECT count(*) FROM \"" + tablename + "\"";
        String selectSQL = "SELECT count(*) FROM " + tablename;
        if(criteria != null)
            selectSQL = selectSQL + " where " + criteria;
//        logger.info("selectSQL= " + selectSQL);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectSQL );
            rs.next();
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            return node.put("numRec", rs.getInt(1));

        } catch (SQLException e) {
        	if(e.getMessage().equals("Connection has been closed."))
        			reconnect();
        	logger.info("----" + e.getMessage() + "------");
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }

    }

    public static String getTablePrimaryKey(String schema, String tablename){
        try {
            // logger.info("getTablePrimaryKey for table " + tablename);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet rs = databaseMetaData.getPrimaryKeys(null,schema, tablename.toLowerCase());
            String columnNamePK = "";
            while(rs.next()){
//                ObjectNode metadata = JsonNodeFactory.instance.objectNode();
                columnNamePK = rs.getString(4);
                // logger.info("Primary key " + columnNamePK);
            }
            return columnNamePK;

        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static JsonNode getTables(String role, String user){
        String selectSQL = "SELECT * from metafrastiki.security_rules where role_id = " + role;
        logger.info("IN GETTABLES: " + selectSQL);
        Statement statement;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectSQL );

            JsonNode res = packageResults(rs);
            if(res.isArray()){
                Iterator<JsonNode> it =((ArrayNode)res).elements();
                while (it.hasNext()){
                    ObjectNode tdata = (ObjectNode)it.next();
                    String tablename = tdata.get("tablename").textValue();
                    DatabaseMetaData databaseMetaData = connection.getMetaData();

                    rs = databaseMetaData.getTables(null, null,  tablename, null);
                    while(rs.next()){     tdata.put("remarks", rs.getString(5));               }
                }
            }

            return res;

        } catch (SQLException e) {
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }

    }

    public static JsonNode getTableInfo(String schema, String tablename){
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        if(tablename.equalsIgnoreCase("undefined")){
        	result.put("error", "Table name is undefined");
        	return result;
        }

        String selectSQL = "SELECT count(*) FROM " + schema + "." + tablename + "";
        ResultSet rs;
        //Logger.info("selectSQL= " + selectSQL);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(selectSQL );
            rs.next();
            result.put("numRec", rs.getInt(1));


            ArrayNode metadataArr = fac.arrayNode();
            

            rs = databaseMetaData.getTables(null, schema,  tablename, null);
            while(rs.next()){
                String  remarks = rs.getString(5);
                result.put("tablename", tablename);
                result.put("remarks", remarks);
                result.put("table_type", rs.getString(4));
            }

            rs = databaseMetaData.getColumns(null, schema,  tablename, null);
            while(rs.next()){
                ObjectNode metadata = JsonNodeFactory.instance.objectNode();

                String  remarks = rs.getString(12);

//                metadata.put("tableName", rs.getString(3));
                metadata.put("columnName", rs.getString(4));
                metadata.put("columnTypeName", rs.getString(6));
                metadata.put("nullable", rs.getString(11));
//                metadata.put("scopeTable", rs.getString(21));
                if(rs.getString(13) != null){
                    metadata.put("column_def", rs.getString(13).split("::")[0]);
                }
                try{
                    JsonNode comment = mapper.readTree(remarks);
//                    Logger.info(comment.get("label") + ", " + comment.get("help"));
                    metadata.put("label", comment.get("label"));
                    metadata.put("help", comment.get("help"));
                    metadata.put("showOnNew", comment.get("showOnNew").asBoolean());
                    metadata.put("getRefData", comment.get("getRefData").asBoolean());
                    if(comment.get("showOnReadList") != null)
                        metadata.put("showOnReadList", comment.get("showOnReadList").asBoolean());
                    else
                        metadata.put("showOnReadList", true);
                }catch(Exception e){
                    metadata.put("remarks", remarks);
                }

//                Logger.info(columnName + ": column type: " + columnType + " -- " + remarks);
                metadataArr.add(metadata);
            }
            result.put("metadata", metadataArr);

            rs = databaseMetaData.getPrimaryKeys(null,schema, tablename);
            while(rs.next()){
                ObjectNode metadata = JsonNodeFactory.instance.objectNode();
                String columnNamePK = rs.getString(4);
                metadata.put("columnName", columnNamePK);

                for (int i=0; i<=metadataArr.size(); i++) {
                    JsonNode obj =  metadataArr.get(i);
                    if(obj.get("columnName").asText().equals(columnNamePK)){
                        ((ObjectNode)obj).put("pkey", true);
                        break;
                    }
                }

                logger.info("Primary key " + columnNamePK);
//                metadataArr.add(metadata);
            }

            //select * from information_schema.view_column_usage where view_name = 'applicationsV'

            rs = databaseMetaData.getImportedKeys(null, schema, tablename);
            while(rs.next()){
                ObjectNode fkdata = JsonNodeFactory.instance.objectNode();
                String pkTableName = rs.getString(3);
                String pkColumnName = rs.getString(4);
                String fkTableName = rs.getString(7);
                String fkColumnName = rs.getString(8);
                fkdata.put("pkTableName", pkTableName);
                fkdata.put("pkColumnName", pkColumnName);

                for (int i=0; i<=metadataArr.size(); i++) {
                    JsonNode obj =  metadataArr.get(i);
                    if(obj.get("columnName").asText().equals(fkColumnName)){
                        if(obj.get("getRefData") != null && obj.get("getRefData").asBoolean()){
                            JsonNode refdata = makeSelectQuery("*", schema, pkTableName, null);
//                            Logger.info(pkTableName + " refdata.asText() " + refdata.isArray());

                            if(refdata.isArray()){
                                for (int k=0; k<=refdata.size(); k++) {
                                    JsonNode obj2 =  refdata.get(k);
                                    if(obj2 != null && obj2.get("label") == null && pkTableName.equals("specializations"))
                                        ((ObjectNode)obj2).put("label", obj2.get("f_category_id") + " " + obj2.get("description"));
                                }
                            }


                            fkdata.put("data", refdata);
                        }
                        ((ObjectNode)obj).put("fkey", fkdata);
                        break;
                    }
                }
//                logger.info("Foreign key " + pkTableName + " --" +pkColumnName + " --" + fkTableName + " --" + fkColumnName);
//                metadataArr.add(metadata)
            }

            rs = databaseMetaData.getCrossReference(null,                    schema,                    tablename,
                                                    null,                    null,                    null);
            ArrayNode crossdataArr = fac.arrayNode();
            while(rs.next()){
                ObjectNode crossdata = JsonNodeFactory.instance.objectNode();
                String FKTABLE_NAME  = rs.getString(7);
                String FKCOLUMN_NAME   = rs.getString(8);
                String PKCOLUMN_NAME = rs.getString(4);

                ResultSet rs2 = databaseMetaData.getTables(null, schema,  FKTABLE_NAME, null);
                while(rs2.next()){
                    String  remarks = rs2.getString(5);
                    crossdata.put("label", remarks );
                }

                crossdata.put("FKTABLE_NAME", FKTABLE_NAME );
                crossdata.put("FKCOLUMN_NAME", FKCOLUMN_NAME );
                crossdata.put("PKCOLUMN_NAME", PKCOLUMN_NAME );



                logger.info("Foreign key " + FKTABLE_NAME  + " --" + FKCOLUMN_NAME  + " --" + "" + " --" + "");
                crossdataArr.add(crossdata);
            }
            result.put("crossdata", crossdataArr);


        } catch (SQLException e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
        }

        return result;

    }





//    public static JsonNode getTableData(String tablename, int id){
//
//        String pkey = getTablePrimaryKey(tablename);
//
//        String getSQL = "SELECT * " + " FROM " + tablename + " where " + pkey + " = ?";
//
//        try {
//            PreparedStatement statement = connection.prepareStatement(getSQL);
//            statement.setInt(1, id);
//            ResultSet rs = statement.executeQuery();
//
//            JsonNode res = packageResults(rs);
//            if(res.isArray() && res.size() > 0){
//                return res.get(0);
//            }else
//                return JsonNodeFactory.instance.objectNode().put("error", "not unique result");
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
//        }
//    }

    private static JsonNode makeSelectQuery(String field, String schema, String tablename, String criteria){

        String selectSQL = "SELECT " + field + " FROM " + schema + "." + tablename;
        if(criteria != null)
            selectSQL = selectSQL + " where " + criteria;
//        logger.info("selectSQL= " + selectSQL);

        return execPlainQuery(selectSQL);
    }

    private static String getCriteriaStr(JsonNode criteria, String schema, String tablename) throws SQLException{
        if(criteria != null && criteria.fields().hasNext()){
            Iterator<Map.Entry<String, JsonNode>> it = criteria.fields();
            String criteriaStr = "";

            while(it.hasNext()){
                Map.Entry<String, JsonNode> elem = it.next();
                String key = elem.getKey();               

                if(!elem.getValue().isContainerNode()){
                    if(elem.getKey().endsWith(".from")){
                        key = elem.getKey().substring(0, elem.getKey().indexOf("."));
                        criteriaStr = criteriaStr + " and " + key  + " >= to_date('" + elem.getValue().asText() + "', 'DD-MM-YYYY')";
                    }else if(elem.getKey().endsWith(".to")){
                        key = elem.getKey().substring(0, elem.getKey().indexOf("."));
                        try{
	                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	                        LocalDate dt = LocalDate.parse(elem.getValue().asText().trim(), fmt);	                       	
	                        String tod = dt.plusDays(1).format(fmt);
	                        criteriaStr = criteriaStr + " and " + key  + " <= to_date('" + tod + "', 'DD-MM-YYYY')";
                        }catch(Exception e){
                        	e.printStackTrace();
                            criteriaStr = criteriaStr + " and " + key  + " <= to_date('" + elem.getValue().asText() + "', 'DD-MM-YYYY')";                                                   	
                        }
                    }else{
                        ResultSet rs = databaseMetaData.getColumns(null, schema,  tablename, null);
                        while(rs.next()){
                            if(rs.getString(4).equalsIgnoreCase(key)){ //String columnName = rs.getString(4);
                                if(rs.getString(6).equalsIgnoreCase("varchar")) //String columnTypeName = rs.getString(6);
                                	criteriaStr = criteriaStr + " and lower(" + key + ")";
                                else
                                	criteriaStr = criteriaStr + " and " + key;
                                break;
                            }
                        }

                        if(!elem.getValue().asText().equals("")){
                            if(elem.getValue().isTextual()) {
                            	if(elem.getValue().asText().contains("%")){
                            		criteriaStr = criteriaStr  + " like lower('" + elem.getValue().asText() + "')";
                            	}else{
                            		criteriaStr = criteriaStr  + " = '" + elem.getValue().asText() + "'";
                            	}
                            }else if(elem.getValue().isNumber()) {
                                criteriaStr = criteriaStr  + " = " + elem.getValue().asText();
                            }
                        }else{
                        	criteriaStr = criteriaStr  + " = '" + elem.getValue().asText() + "'";
                        }
                    }
                }else if(elem.getKey().equalsIgnoreCase("filterCriteria")){

                    JsonNode filterCriteria = elem.getValue();
                    
                    Iterator<Map.Entry<String, JsonNode>> it2 = filterCriteria.fields();
                    while(it2.hasNext()){
                        Map.Entry<String, JsonNode> elem2 = it2.next();
                        String key2 = elem2.getKey();
                        criteriaStr = criteriaStr + " and " + key2;
                        if(!elem2.getValue().asText().equals("")){
                            if(elem2.getValue().isTextual()) {
                                criteriaStr = criteriaStr  + " like '%" + elem2.getValue().asText() + "%'";
                            }
                        }

                    }


                }

            }
            if(criteriaStr.startsWith(" and"))
                return criteriaStr.substring(4);
            else return criteriaStr;
        }else
            return "";
    }

//    public static JsonNode getTableData2(String field, String tableName, JsonNode criteria){
//        String selectSQL = "SELECT " + field + " FROM " + tableName;
//
//        String critStr = getCriteriaStr(criteria);
//        if(critStr.length() > 0)
//            selectSQL = selectSQL + " WHERE " + critStr;
//
//        return getTableData(selectSQL);
//    }

    public static JsonNode callFunction(String functionName, JsonNode parameters){
        String selectSQL = "SELECT \"" + functionName;
        if(parameters != null){
            Iterator<Map.Entry<String, JsonNode>> it = parameters.fields();
            String parametersStr = "";

            while(it.hasNext()){
                Map.Entry<String, JsonNode> elem = it.next();
                parametersStr = parametersStr + ", " + elem.getKey() + " := '" +  elem.getValue().asText() + "'";
            }
            selectSQL = selectSQL + "\"(" + parametersStr.substring(1) + ")";

        }
        return execPlainQuery(selectSQL);
    }


    private static JsonNode execPlainQuery(String fullQuery){
        Statement statement = null;
        logger.info("fullQuery= " + fullQuery);
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(fullQuery );

            return packageResults(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }
    }

//    public static JsonNode getTableData(String field, String tablename, String orderBy, int limit, int offset){
//
//        if(offset < 0) offset = 0;
//        if(limit < 0) limit = 1;
//
//        int maxrows = getTableNumRecords(tablename, null).get("numRec").asInt();
//
//        if(offset > maxrows) offset = maxrows - limit;
//
//        String selectSQL = "SELECT " + field + " FROM \"" + tablename + "\"";
//        if(orderBy.length() > 0)
//            selectSQL = selectSQL + " order by " + orderBy;
//        selectSQL = selectSQL + " limit " + limit + " offset " + offset;
//        //logger.info("selectSQL= " + selectSQL);
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery(selectSQL );
//
//            return packageResults(rs);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
//        }
//
//    }
    
    private static String checkOrderBy(String schema, String tablename, String orderBy) throws SQLException{
    	String res = "";
        if(orderBy.length() > 0){
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            boolean isView = false;
            ResultSet rs = databaseMetaData.getTables(null, schema,  tablename, null);
            while(rs.next()){
                String tabletype = rs.getString(4);
                if(tabletype.equalsIgnoreCase("VIEW")){
                    isView = true;
                }

            }

            rs = databaseMetaData.getColumns(null, schema,  tablename, null);
            boolean foundColumn = false;
            while(rs.next()){
                if(rs.getString(4).equals(orderBy.substring(0, orderBy.indexOf(" ")))){
                    foundColumn = true;
                    break;
                }
            }
            if(foundColumn || isView)
                res = " order by " + orderBy;

        }
		return res;
    }
    
    public static JsonNode getTableDataStrict(String field, String schema, String tablename, JsonNode criteria, String orderBy, int limit, int offset){
        if(offset < 0) offset = 0;
        if(limit < 0) limit = 1000;
        String tableFullName = schema + "." +tablename;
        int maxrows = getTableNumRecords(tableFullName, null).get("numRec").asInt();

        if(offset > maxrows) offset = maxrows - limit;
        if(offset < 0) offset = maxrows-1;
        
        String selectSQL = "SELECT " + field + " FROM " + tableFullName ;
        try {
        	
        	String critStr = "";
	        if(criteria.isArray()){
	            for (JsonNode elem : criteria) {
	            	if(critStr.length() > 0)
	            		critStr = critStr + " and (" + getCriteriaStr(elem, schema, tablename) + ")";
	            	else
	            		critStr = "(" + getCriteriaStr(elem, schema, tablename) + ")";
	            }
	        }else{
	        	critStr = getCriteriaStr(criteria, schema, tablename);
	        }
	 
	        if(critStr.length() > 0)
	            selectSQL = selectSQL + " WHERE " + critStr;
        
        	selectSQL = selectSQL + checkOrderBy(schema, tablename, orderBy) + " limit " + limit + " offset " + offset;;
        
        	return execPlainQuery(selectSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }
    }

    // most commonly used !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static JsonNode getTableData(String field, String schema, String tablename, JsonNode criteria, String orderBy, int limit, int offset){

        if(offset < 0) offset = 0;
        if(limit < 0) limit = 1000;
        String tableFullName = schema + "." +tablename;
        int maxrows = getTableNumRecords(tableFullName, null).get("numRec").asInt();

        if(offset > maxrows) offset = maxrows - limit;
        if(offset < 0) offset = maxrows-1;

//        String selectSQL = "SELECT " + field + " FROM ONLY \"" + tablename + "\"";
        String selectSQL = "SELECT " + field + " FROM " + tableFullName ;

//        logger.info("criteria " + criteria.toString());
        try {        
        	String critStr = "";
	        if(criteria.isArray()){
	            for (JsonNode elem : criteria) {
	            	if(critStr.length() > 0)
	            		critStr = critStr + " or (" + getCriteriaStr(elem, schema, tablename) + ")";
	            	else
	            		critStr = "(" + getCriteriaStr(elem, schema, tablename) + ")";
	            }
	        }else{
	        	critStr = getCriteriaStr(criteria, schema, tablename);
	        }
	 
	        if(critStr.length() > 0)
	            selectSQL = selectSQL + " WHERE " + critStr;
	
//	        if(orderBy.length() > 0){
//                DatabaseMetaData databaseMetaData = connection.getMetaData();
//                boolean isView = false;
//                ResultSet rs = databaseMetaData.getTables(null, schema,  tablename, null);
//                while(rs.next()){
//                    String tabletype = rs.getString(4);
//                    if(tabletype.equalsIgnoreCase("VIEW")){
//                        isView = true;
//                    }
//
//                }
//
//                rs = databaseMetaData.getColumns(null, schema,  tablename, null);
//                boolean foundColumn = false;
//                while(rs.next()){
//                    if(rs.getString(4).equals(orderBy.substring(0, orderBy.indexOf(" ")))){
//                        foundColumn = true;
//                        break;
//                    }
//                }
//                if(foundColumn || isView)
//                    selectSQL = selectSQL + " order by " + orderBy;
//
//	
//	        }
	        selectSQL = selectSQL + checkOrderBy(schema, tablename, orderBy);
	        
	        selectSQL = selectSQL + " limit " + limit + " offset " + offset;
	        //logger.info("selectSQL= " + selectSQL);
	        Statement statement = null;

            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectSQL );

            return packageResults(rs);

        } catch (SQLException e) {
        	if(e.getMessage().equals("Connection has been closed."))
    			reconnect();
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }

    }

    public static String getUniqueResult(String field, String schema, String tablename, JsonNode criteria){
        JsonNode resArr = getTableData(field, schema, tablename, criteria, "", -1, -1);
        if(resArr.isArray()){
            for (JsonNode elem : resArr) {
                return elem.get(field).asText();
            }
        }else
            return resArr.toString();
        return "";
    }
    
    public static JsonNode getUniqueJsonResult(String field, String schema, String tablename, JsonNode criteria){
        JsonNode resArr = getTableData(field, schema, tablename, criteria, "", -1, -1);
        //System.out.println("getUniqueJsonResult: " + resArr);
        if(resArr.isArray()){
            for (JsonNode elem : resArr) {
                return elem;
            }
        }else if(resArr.isContainerNode()){
            return resArr;
        }else{
        	System.out.println("getUniqueJsonResult: " + resArr);
        }
		return null;
    }


    public static Object getOneResult(String field, String schema, String tablename, JsonNode criteria){
        JsonNode resArr = getTableData(field, schema, tablename, criteria, "", -1, -1);
        if(resArr.isArray()){
            for (JsonNode elem : resArr) {
                JsonNode res = elem.get(field);
                if(res.isLong())
                    return res.asLong();
                else if(res.isTextual())
                    return res.asText();
                else if(res.isDouble())
                    return res.asDouble();
                else return null;
            }
        }
        return null;
    }

    public static JsonNode packageResults(ResultSet rs){
        ArrayNode arrayObj = fac.arrayNode();
        try {
            ResultSetMetaData metadata = rs.getMetaData();

            while (rs.next()) {
                ObjectNode at_match = JsonNodeFactory.instance.objectNode();
                for (int i=1; i<=metadata.getColumnCount(); i++) {
                    String columnName = metadata.getColumnName(i);
                   
                    int val = 0;    String valStr = ""; boolean valb;
                    switch(metadata.getColumnType(i)){
                        case Types.VARCHAR : valStr = rs.getString(columnName); if (!rs.wasNull()) {   at_match.put(columnName, valStr); }
                            break;
                        case Types.NUMERIC : val = rs.getInt(columnName); if (!rs.wasNull()) {   at_match.put(columnName, val); }
                            break;
                        case Types.BIGINT : val = rs.getInt(columnName); if (!rs.wasNull()) {   at_match.put(columnName, val); }
                            break;
                        case Types.BOOLEAN : valb = rs.getBoolean(columnName); if (!rs.wasNull()) {   at_match.put(columnName, valb); }
                            break;
                        case Types.TIMESTAMP : if(rs.getTimestamp(columnName) != null)   at_match.put(columnName, rs.getTimestamp(columnName).getTime());
                            break;
                        case Types.FLOAT : float valf = rs.getFloat(columnName); if (!rs.wasNull()) {   at_match.put(columnName, valf); }
                            break;
                        case Types.DOUBLE : double vald = rs.getDouble(columnName); if (!rs.wasNull()) {   at_match.put(columnName, vald); }
                            break;
                        case Types.BIT : valb = rs.getBoolean(columnName); if (!rs.wasNull()) {   at_match.put(columnName, valb); }
                            break;
                        case Types.INTEGER : val = rs.getInt(columnName); if (!rs.wasNull()) {   at_match.put(columnName, val); }
                            break;
                        case Types.OTHER : valStr = rs.getString(columnName);
//                                System.out.println("column name " + columnName + " column type: " + metadata.getColumnTypeName(i) + " " + metadata.getColumnType(i));
                                if((metadata.getColumnTypeName(i).equalsIgnoreCase("json") || metadata.getColumnTypeName(i).equalsIgnoreCase("jsonb")) && valStr != null){
                                        try {
                                            JsonNode match = mapper.readTree(valStr);
                                            at_match.put(columnName, match);
                                        } catch (IOException e) { at_match.put(columnName, valStr); }
                                }
                                else if (!rs.wasNull()) {   at_match.put(columnName, valStr); }
                            break;
                        default : logger.info("unknown column type " + columnName + ", column type: " + metadata.getColumnTypeName(i));
                    }


                }
                arrayObj.add(at_match);
            }
            rs.close();
            return arrayObj;
        } catch (SQLException e) {
        	if(e.getMessage().equals("Connection has been closed."))
    			reconnect();
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }
    }

    public static JsonNode getJsonNode(String data){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(data);

        } catch (IOException e) {
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }
    }

    public static int getNextValue(String seqName) throws SQLException{
        String sql = "select nextval(?)";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, seqName);
        ResultSet rs = statement.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static JsonNode deleteTableData(String schema, String tablename, String id){

        String pkey = getTablePrimaryKey(schema, tablename);

        String deleteSQL = "delete " + " FROM " + schema + "." + tablename + " where " + pkey + " = ?";

        System.out.println("deleteSQL: " + deleteSQL + " " + id.toString() + "---" + id);



        try {
            PreparedStatement statement = connection.prepareStatement(deleteSQL);
            UUID uuid = UUID.fromString(id);
            statement.setObject(1, uuid);
//            if(id.get("id").isNumber())
//                statement.setInt(1, id.get("id").asInt());
//            if(id.get("id").isTextual())
//                statement.setString(1, id.get("id").asText());
            boolean result = statement.execute();
            return JsonNodeFactory.instance.objectNode().put("res", result);
        } catch (SQLException e) {
        	if(e.getMessage().equals("Connection has been closed."))
    			reconnect();
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }
//        return JsonNodeFactory.instance.objectNode().put("error", "TO-DO");
    }

    public static JsonNode deleteTableDataCriteria(String schema, String tableName, JsonNode criteria){
        DatabaseMetaData md = null;
        try {
            md = connection.getMetaData();
            Iterator<Map.Entry<String, JsonNode>> it2 = criteria.fields();
            String criteriaNames = "";

            while(it2.hasNext()){
                Map.Entry<String, JsonNode> elem = it2.next();
                criteriaNames = criteriaNames + " and " + elem.getKey() + "= ?";
            }
            if(criteria.size() > 0){
                String updateSQL = "delete from " + schema + "." + tableName +
                        " where " + criteriaNames.substring(4);

                logger.info("deleteSQL = " + updateSQL);

                PreparedStatement statement = connection.prepareStatement(updateSQL);
                int k = 1;

                it2 = criteria.fields();
                while(it2.hasNext()){
                    Map.Entry<String, JsonNode> elem = it2.next();
                    statement = setData(statement, md, schema, tableName, k++, elem);
                }

                statement.executeUpdate();
                statement.close();
            }
            return JsonNodeFactory.instance.objectNode().put("result", "success");
        } catch (SQLException e) {
        	if(e.getMessage().equals("Connection has been closed."))
    			reconnect();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }
    }

    public static JsonNode updateTable(JsonNode data, String schema, String tableName, JsonNode criteria, String returning){
        DatabaseMetaData md = null;
        try {
            md = connection.getMetaData();
//            logger.info("DATA: " + data.toString() + " ------- " + criteria.toString());

            Iterator<Map.Entry<String, JsonNode>> it = data.fields();

            Iterator<Map.Entry<String, JsonNode>> it2 = criteria.fields();

            String columnNames = "", criteriaNames = "";

            while(it.hasNext()){
                Map.Entry<String, JsonNode> elem = it.next();
                columnNames = columnNames + ", " + elem.getKey() + "= ?";
                //columnValues = columnValues + ", ?";
            }
            while(it2.hasNext()){
                Map.Entry<String, JsonNode> elem = it2.next();
                criteriaNames = criteriaNames + " and " + elem.getKey() + "= ?";
                //columnValues = columnValues + ", ?";
            }
            if(data.size() > 0 && criteria.size() > 0){
                String updateSQL = "update " + schema + "." + tableName + " set " + columnNames.substring(2) +
                        " where " + criteriaNames.substring(4) + " returning " + returning;

//                logger.info("updateSQL = " + updateSQL);

                PreparedStatement statement = connection.prepareStatement(updateSQL);
                int k = 1;
                it = data.fields();
                while(it.hasNext()){
                    Map.Entry<String, JsonNode> elem = it.next();
                    statement = setData(statement, md, schema, tableName, k++, elem);
                }

                it2 = criteria.fields();
                while(it2.hasNext()){
                    Map.Entry<String, JsonNode> elem = it2.next();
                    statement = setData(statement, md, schema, tableName, k++, elem);
                }


//                statement.executeUpdate();
//                statement.close();
                
                ResultSet rs = statement.executeQuery();
                if (rs.next()){
                    Object ret = rs.getObject(1);
//                    System.out.println("ret : " + ret.getClass().getName());
                    statement.close();
                    if (ret.getClass().equals(Integer.class)) {
                        int retInt = Integer.parseInt(ret.toString());
                        return JsonNodeFactory.instance.objectNode().put(returning, retInt);
                    }
                    else {
                        return JsonNodeFactory.instance.objectNode().put(returning, ret.toString());
                    }


                }else
                    return JsonNodeFactory.instance.objectNode().put("error", -100);
            }else{
            return JsonNodeFactory.instance.objectNode().put("error", "Nothing to update");
            }
        } catch (SQLException e) {
        	if(e.getMessage().equals("Connection has been closed."))
    			reconnect();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }
    }

//    public static int insert(JsonNode data, String tableName)  throws SQLException{
//
//        DatabaseMetaData md = connection.getMetaData();
//
//        String id = getTablePrimaryKey(tableName);
//
//        Iterator<Map.Entry<String, JsonNode>> it = data.fields();
//
//        String columnNames = "", columnValues = "";
//
//        while(it.hasNext()){
//            Map.Entry<String, JsonNode> elem = it.next();
//            columnNames = columnNames + ", " + elem.getKey();
//            columnValues = columnValues + ", ?";
//        }
//        String insertSQL = "insert into " + tableName + " (" + columnNames.substring(2) +
//                ") values (" + columnValues.substring(2) + ") returning " + id;
//
//        if(!tableName.equals("logs"))
//            logger.info("..insertSQL = " + insertSQL);
//
//        PreparedStatement statement = connection.prepareStatement(insertSQL);
//        int k = 1;
//        it = data.fields();
//        while(it.hasNext()){
//            Map.Entry<String, JsonNode> elem = it.next();
//            statement = setData(statement, md, schema, tableName, k++, elem);
//        }
//
//        ResultSet rs = statement.executeQuery();
////        statement.getResultSet();
//        if (rs.next()){
//            int newId = rs.getInt(1);
//            return newId;
//        }else
//            return -1;
//
//    }

    private static PreparedStatement setData(PreparedStatement statement, DatabaseMetaData md, String schema, String tableName, int offset, Map.Entry<String, JsonNode> elem) throws SQLException {
        ResultSet colMetaData = null;

        colMetaData = md.getColumns(null, schema, tableName, elem.getKey());

        JsonNode value = elem.getValue();

        String key = elem.getKey();
        if(colMetaData.next()){
            int columnType = colMetaData.getInt(5);
            String columnName = colMetaData.getString(6);
            
            switch(columnType){
                case Types.VARCHAR : statement.setString(offset, value.asText());
                    break;
                case Types.NUMERIC : statement.setInt(offset, value.asInt());
                    break;
                case Types.BIGINT : if(value.isNull()){statement.setNull(offset, Types.NULL);}else{statement.setInt(offset, value.asInt());};
                    break;
                case Types.INTEGER : if(value.isNull()){statement.setNull(offset, Types.NULL);}else{statement.setInt(offset, value.asInt());};
                    break;
                case Types.BOOLEAN : statement.setBoolean(offset, value.asBoolean());
                    break;
                case Types.BIT : statement.setBoolean(offset, value.asBoolean());
                    break;
                case Types.TIMESTAMP : 
                    ISO8601DateFormat df = new ISO8601DateFormat();
                    //logger.info("columnName " + columnName + " columnType " + columnType + " value=" + value.asText());
                    //2015-12-21T22:00:00.000Z
                    //yyyy-MM-dd'T'HH:mm:ss.SSSZ

                    try {
                        LocalDateTime res = LocalDateTime.parse(value.asText(), formatter);
                        //logger.info("1 DATE IS " + res.format(formatter));

                        // java.util.Date d1 = df.parse(value.asText());
                        // Instant instant = Instant.ofEpochMilli(d1.getTime());
                        // LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        // res = res.plusHours(2);
                        statement.setTimestamp(offset, new Timestamp(res.atZone(ZoneId.systemDefault()).toEpochSecond()));
                    } catch (Exception e) {
                        try{
                            LocalDateTime res = LocalDateTime.parse(value.asText(), shdeformatter);
                            long lts = res.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            statement.setTimestamp(offset, new Timestamp(lts));
                        }catch (Exception e2) {
                            //logger.info("exception DATE IS " + value.asLong());
                            statement.setTimestamp(offset, new Timestamp(value.asLong()));
                        }
                    }
                    break;
                case Types.DOUBLE : statement.setDouble(offset, value.asDouble());
                    break;
                case Types.OTHER :
//                	logger.info("other  " + columnName + " for key " + key + " with value " + value);
                    if(columnName.contains("uuid")){
                        UUID uuid = UUID.fromString(value.asText());
                        statement.setObject(offset, uuid);
                    }
                    else if(columnName.equalsIgnoreCase("json") || columnName.equalsIgnoreCase("jsonb")){
//                        logger.info("jsonb  " + columnName + " for key " + key + " with value " + value);
                        PGobject pgObject = new PGobject();
                        pgObject.setType(columnName);
                        pgObject.setValue(value.toString());
                        statement.setObject(offset, pgObject);
                    }
                    break;
                default : logger.info("unknown column type " + columnType + " name: " + columnName + " for key " + key + " with value " + value + " Types.OTHER " + Types.OTHER);
            }
        }else if(value.isNull()){
            statement.setNull(offset, Types.NULL);
        }else if(value.isInt()){
            statement.setInt(offset, value.asInt());
        }else if(value.isLong()){
            statement.setTimestamp(offset, new Timestamp(value.asLong()));
        }else if(value.isTextual()){
            statement.setString(offset, value.asText());
        }else if(value.isDouble()){
            statement.setDouble(offset, value.asDouble());
        }

        return statement;
    }

    public static JsonNode insert(JsonNode data, String schema, String tableName, String returning) {

        DatabaseMetaData md = null;
        try {
            md = connection.getMetaData();

    //        ResultSet resCols = md.getColumns(null, null, tableName, "%");

            logger.info("DATA: " + data.toString());

            Iterator<Map.Entry<String, JsonNode>> it = data.fields();

            String columnNames = "", columnValues = "";

            while(it.hasNext()){
                Map.Entry<String, JsonNode> elem = it.next();
                columnNames = columnNames + ", " + elem.getKey();
                columnValues = columnValues + ", ?";
            }
            String insertSQL = "insert into " + schema + "." + tableName + " (" + columnNames.substring(2) +
                    ") values (" + columnValues.substring(2) + ") returning " + returning;

            logger.info("insertSQL = " + insertSQL);

            PreparedStatement statement = connection.prepareStatement(insertSQL);
            int k = 1;
            it = data.fields();
            while(it.hasNext()){
                Map.Entry<String, JsonNode> elem = it.next();
                statement = setData(statement, md, schema, tableName, k++, elem);
            }

//            statement.executeUpdate();
//            ResultSet rs = statement.getResultSet();
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                Object ret = rs.getObject(1);
//                System.out.println("ret : " + ret.getClass().getName());
                if (ret.getClass().equals(Integer.class)) {
                    int retInt = Integer.parseInt(ret.toString());
                    return JsonNodeFactory.instance.objectNode().put(returning, retInt);
                }
                else {
                    return JsonNodeFactory.instance.objectNode().put(returning, ret.toString());
                }


            }else
                return JsonNodeFactory.instance.objectNode().put("error", -100);


        } catch (SQLException e) {
        	if(e.getMessage().equals("Connection has been closed."))
    			reconnect();
            e.printStackTrace();
            return JsonNodeFactory.instance.objectNode().put("error", e.getMessage());
        }

    }

}
