import bean.Rank;
import bean.newpoiDbf;
import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Title: poiMatch
 * @Description: poi_cms.shp与Rank.csv的匹配赋值
 * @Company: www.leador.com.cn
 * @Date: 2019-07-23 09:35
 * @author: wangjiyuan
 * @Version: 1.0
 **/
public class poiMatch {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("请输入Rank.csv的路径:");
        String p1 = input.next();
        System.out.println("请输入poi_cms.shp的路径:");
        String p2 = input.next();
        System.out.println("请输入poi_cms.shp的编码(GBK/UTF-8):");
        String p3 = input.next();
        System.out.println("请输入新的shp文件的路径:");
        String p4 = input.next();
        System.out.println("请输入新的shp文件的编码(GBK/UTF-8):");
        String p5 = input.next();
        /*String rankPath = "E:\WJY\点抽稀处理工具\Rank.csv";
        String shpInPath = "E:\WJY\点抽稀处理工具\测试数据\H48F017017\poi_cms.shp";
        E:\WJY\点抽稀处理工具\poiMatch\四川省全部poi秒\四川省全部poi.shp
        E:\WJY\点抽稀处理工具\四川省加密前POI拼图全部\poi_cms.shp
        String shpOutPath = "E:\WJY\点抽稀处理工具\New_poi_cms.shp";*/
        String rankPath = p1;
        String shpInPath = p2;
        String shpInCharset = p3;
        String shpOutPath = p4;
        String shpOutCharset = p5;
        poiMatchOperate(rankPath, shpInPath, shpInCharset, shpOutPath, shpOutCharset);
    }
    /**
    * @Description: 读取Rank表为list
    * @Param: rankPath, charsetString
    * @return: List<Rank>
    * @Author: Wang Jiyuan
    * @Date: 2019/7/23 
    **/ 
    private static List<Rank> readRank(String rankPath, String charsetString) throws Exception{
        //读取CSV文件
        InputStreamReader input = new InputStreamReader(new FileInputStream(rankPath),charsetString);
        BufferedReader br = new BufferedReader(input);
        List<Rank> rankList = new ArrayList<>();
        String lineCSV = null;
        int count = 0;
        System.out.println("-----------------------开始读取Rank表--------------------------");
        while ((lineCSV = br.readLine()) != null){
            Rank rank = new Rank();
            String[] arr = lineCSV.split(",");
            rank.setCategoryName(arr[0]);
            rank.setCategory(arr[1]);
            rank.setType(arr[2]);
            rank.setLevel(arr[3]);
            rank.setBrand(arr[4]);
            rank.setMinScaleID(arr[5]);
            rank.setMaxScaleID(arr[6]);
            rank.setRank(arr[7]);
            rankList.add(rank);
            count++;
            if (count != 1) {
                System.out.println(lineCSV);
            }
        }
        rankList.remove(0);
        System.out.println("读取Rank总记录数为：" + rankList.size());
        return rankList;
    }

    /**
    * @Description: poi匹配主方法
    * @Param: rankPath , shpPath , charsetString
    * @return:
    * @Author: Wang Jiyuan
    * @Date: 2019/7/23
    **/
    public static void poiMatchOperate(String rankPath,String shpInPath,String shpInCharset,String shpOutPath,String shpOutCharset){
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;
        SimpleFeatureIterator itertor = null;
        try {
            List<Rank> rankList = readRank(rankPath, shpInCharset);
            //读取shp文件
            File file = new File(shpInPath);
            ShapefileDataStore shapefileDataStore = null;
            shapefileDataStore = new ShapefileDataStore(file.toURI().toURL());
            Charset charsetr = Charset.forName(shpInCharset);
            Charset charsetw = Charset.forName(shpOutCharset);
            shapefileDataStore.setCharset(charsetr);
            String typeName = shapefileDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = null;
            featureSource = shapefileDataStore.getFeatureSource(typeName);
            SimpleFeatureCollection result = featureSource.getFeatures();
            itertor = result.features();
            //创建shp文件
            File fileBuf = new File(shpOutPath);
            Map<String, Serializable> params = new HashMap<>();
            params.put(ShapefileDataStoreFactory.URLP.key,fileBuf.toURI().toURL());
            ShapefileDataStore dataStore = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
            //获取原shp文件字段名
            SimpleFeatureType sft = featureSource.getSchema();
            List<AttributeDescriptor> attrs = sft.getAttributeDescriptors();
            //定义图形和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.setCRS(DefaultGeographicCRS.WGS84);
            tb.setName("shapefile");
            for(int i=0;i<attrs.size();i++){
                AttributeDescriptor attr = attrs.get(i);
                tb.add(attr);
            }
            tb.add("CategoryNa",String.class);
            tb.add("Category",String.class);
            tb.add("MinScaleID",String.class);
            tb.add("MaxScaleID",String.class);
            tb.add("Rank",String.class);
            tb.add("Delete",String.class);
            dataStore.createSchema(tb.buildFeatureType());
            //设置编码
            dataStore.setCharset(charsetw);
            //设置Writer，遍历原feature，添加到新feature
            writer = dataStore.getFeatureWriter(dataStore.getTypeNames()[0], Transaction.AUTO_COMMIT);
            List<newpoiDbf> dbfList = new ArrayList<>();
            System.out.println("-----------------------开始匹配poi_cms.shp--------------------------");
            while (itertor.hasNext()){
                newpoiDbf newpoiDbf = new newpoiDbf();
                SimpleFeature feature = itertor.next();
                System.out.println("--------正在匹配--------"+feature.getAttribute("Name_chn"));
                SimpleFeature featureBuf = writer.next();
                featureBuf.setAttribute("the_geom",feature.getAttribute("the_geom"));
                if (feature.getAttribute("POIID") == null || feature.getAttribute("POIID").equals("")){
                    featureBuf.setAttribute("POIID","");
                }else {
                    featureBuf.setAttribute("POIID",feature.getAttribute("POIID"));
                    newpoiDbf.setPOIID(feature.getAttribute("POIID").toString());
                }
                if (feature.getAttribute("GUIID") == null || feature.getAttribute("GUIID").equals("")){
                    featureBuf.setAttribute("GUIID","");
                }else {
                    featureBuf.setAttribute("GUIID",feature.getAttribute("GUIID"));
                    newpoiDbf.setGUIID(feature.getAttribute("GUIID").toString());
                }
                if (feature.getAttribute("Name_chn") == null || feature.getAttribute("Name_chn").equals("")){
                    featureBuf.setAttribute("Name_chn","");
                }else {
                    featureBuf.setAttribute("Name_chn",feature.getAttribute("Name_chn"));
                    newpoiDbf.setName_chn(feature.getAttribute("Name_chn").toString());
                }
                if (feature.getAttribute("Name_trd") == null || feature.getAttribute("Name_trd").equals("")){
                    featureBuf.setAttribute("Name_trd","");
                }else {
                    featureBuf.setAttribute("Name_trd",feature.getAttribute("Name_trd"));
                    newpoiDbf.setName_trd(feature.getAttribute("Name_trd").toString());
                }
                if (feature.getAttribute("Name_eng") == null || feature.getAttribute("Name_eng").equals("")){
                    featureBuf.setAttribute("Name_eng","");
                }else {
                    featureBuf.setAttribute("Name_eng",feature.getAttribute("Name_eng"));
                    newpoiDbf.setName_eng(feature.getAttribute("Name_eng").toString());
                }
                if (feature.getAttribute("X_coord") == null || feature.getAttribute("X_coord").equals("")){
                    featureBuf.setAttribute("X_coord","");
                }else {
                    featureBuf.setAttribute("X_coord",feature.getAttribute("X_coord"));
                    newpoiDbf.setX_coord(feature.getAttribute("X_coord").toString());
                }
                if (feature.getAttribute("Y_coord") == null || feature.getAttribute("Y_coord").equals("")){
                    featureBuf.setAttribute("Y_coord","");
                }else {
                    featureBuf.setAttribute("Y_coord",feature.getAttribute("Y_coord"));
                    newpoiDbf.setY_coord(feature.getAttribute("Y_coord").toString());
                }
                if (feature.getAttribute("Type").equals("")){
                    featureBuf.setAttribute("Type","*");
                    newpoiDbf.setType("*");
                }else {
                    featureBuf.setAttribute("Type",feature.getAttribute("Type"));
                    newpoiDbf.setType(feature.getAttribute("Type").toString());
                }
                if (feature.getAttribute("Level").equals("")){
                    featureBuf.setAttribute("Level","*");
                    newpoiDbf.setLevel("*");
                }else {
                    featureBuf.setAttribute("Level",feature.getAttribute("Level"));
                    newpoiDbf.setLevel(feature.getAttribute("Level").toString());
                }
                if (feature.getAttribute("Brand").equals("")){
                    featureBuf.setAttribute("Brand","*");
                    newpoiDbf.setBrand("*");
                }else {
                    featureBuf.setAttribute("Brand",feature.getAttribute("Brand"));
                    newpoiDbf.setBrand(feature.getAttribute("Brand").toString());
                }
                featureBuf.setAttribute("priority",feature.getAttribute("priority"));
                newpoiDbf.setPriority(feature.getAttribute("priority").toString());
                boolean bool = false;
                for (Rank rank : rankList){
                    if (rank.getType().equals(featureBuf.getAttribute("Type").toString()) && rank.getLevel().equals(featureBuf.getAttribute("Level").toString()) && rank.getBrand().equals(featureBuf.getAttribute("Brand").toString())){
                        featureBuf.setAttribute("CategoryNa",rank.getCategoryName());
                        newpoiDbf.setCategoryNa(rank.getCategoryName());
                        featureBuf.setAttribute("Category",rank.getCategory());
                        newpoiDbf.setCategory(rank.getCategory());
                        featureBuf.setAttribute("MinScaleID",rank.getMinScaleID());
                        newpoiDbf.setMinScaleID(rank.getMinScaleID());
                        featureBuf.setAttribute("MaxScaleID",rank.getMaxScaleID());
                        newpoiDbf.setMaxScaleID(rank.getMaxScaleID());
                        featureBuf.setAttribute("Rank",rank.getRank());
                        newpoiDbf.setRank(rank.getRank());
                        System.out.println(">>>>>>>>>>>>>>>>>>成功>>>>>>>>>>>>>>>>>>");
                        bool = true;
                    }
                }
                if (!bool){
                    System.out.println("<<<<<<<<<<<<<<<<<失败<<<<<<<<<<<<<<<<<");
                }
                dbfList.add(newpoiDbf);
            }
            System.out.println("-----------------------结束匹配poi_cms.shp--------------------------");
            writer.write();
            //writeDbfFile(dbfList,shpOutPath);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                writer.close();
                itertor.close();
            }catch (Exception e){

            }
        }
    }

    /**
    * @Description: 重写DBF文件
    * @Param: List<newpoiDbf>, shpOutPath
    * @return:
    * @Author: Wang Jiyuan
    * @Date: 2019/7/23
    **/
    private static void writeDbfFile(List<newpoiDbf> dbflist, String shpOutPath){
        //重新生成dbf
        String[] strings = shpOutPath.split("\\\\");
        String fileName = strings[strings.length-1];
        String name = fileName.split("/.")[0];
        String rootName = shpOutPath.replaceAll(strings[strings.length-1],"");
        File dbffile = new File(rootName+name+".dbf");
        if(dbffile.exists()){
            dbffile.delete();
        }
        DBFWriter dbfWriter = null;
        try {
            //定义DBF文件字段
            DBFField[] fields = new DBFField[17];
            //分别定义各个字段信息，setFieldName和setName作用相同
            fields[0] = new DBFField();
            fields[0].setName("POIID");
            fields[0].setType(DBFDataType.VARCHAR);
            fields[0].setLength(64);

            fields[1] = new DBFField();
            fields[1].setName("GUIID");
            fields[1].setType(DBFDataType.VARCHAR);
            fields[1].setLength(64);

            fields[2] = new DBFField();
            fields[2].setName("Name_chn");
            fields[2].setType(DBFDataType.VARCHAR);
            fields[2].setLength(64);

            fields[3] = new DBFField();
            fields[3].setName("Name_trd");
            fields[3].setType(DBFDataType.VARCHAR);
            fields[3].setLength(64);

            fields[4] = new DBFField();
            fields[4].setName("Name_eng");
            fields[4].setType(DBFDataType.VARCHAR);
            fields[4].setLength(64);

            fields[5] = new DBFField();
            fields[5].setName("X_coord");
            fields[5].setType(DBFDataType.VARCHAR);
            fields[5].setLength(64);

            fields[6] = new DBFField();
            fields[6].setName("Y_coord");
            fields[6].setType(DBFDataType.VARCHAR);
            fields[6].setLength(64);

            fields[7] = new DBFField();
            fields[7].setName("Type");
            fields[7].setType(DBFDataType.VARCHAR);
            fields[7].setLength(64);

            fields[8] = new DBFField();
            fields[8].setName("Level");
            fields[8].setType(DBFDataType.VARCHAR);
            fields[8].setLength(64);

            fields[9] = new DBFField();
            fields[9].setName("Brand");
            fields[9].setType(DBFDataType.VARCHAR);
            fields[9].setLength(64);

            fields[10] = new DBFField();
            fields[10].setName("priority");
            fields[10].setType(DBFDataType.VARCHAR);
            fields[10].setLength(64);

            fields[11] = new DBFField();
            fields[11].setName("CategoryNa");
            fields[11].setType(DBFDataType.VARCHAR);
            fields[11].setLength(64);

            fields[12] = new DBFField();
            fields[12].setName("Category");
            fields[12].setType(DBFDataType.VARCHAR);
            fields[12].setLength(64);

            fields[13] = new DBFField();
            fields[13].setName("MinScaleID");
            fields[13].setType(DBFDataType.VARCHAR);
            fields[13].setLength(64);

            fields[14] = new DBFField();
            fields[14].setName("MaxScaleID");
            fields[14].setType(DBFDataType.VARCHAR);
            fields[14].setLength(64);

            fields[15] = new DBFField();
            fields[15].setName("Rank");
            fields[15].setType(DBFDataType.VARCHAR);
            fields[15].setLength(64);

            fields[16] = new DBFField();
            fields[16].setName("Delete");
            fields[16].setType(DBFDataType.VARCHAR);
            fields[16].setLength(64);

            //定义DBFWriter实例用来写DBF文件
            dbfWriter = new DBFWriter(dbffile,Charset.forName("GBK"));
            //把字段信息写入DBFWriter实例，即定义表结构
            dbfWriter.setFields(fields);
            //一条条的写入记录
            Object[] rowData = null;
            int id = 0;
            for (newpoiDbf newpoiDbf : dbflist){
                rowData = new String[6];
                rowData[0] = newpoiDbf.getPOIID();
                rowData[1] = newpoiDbf.getGUIID();
                rowData[2] = newpoiDbf.getName_chn();
                rowData[3] = newpoiDbf.getName_trd();
                rowData[4] = newpoiDbf.getName_eng();
                rowData[5] = newpoiDbf.getX_coord();
                rowData[6] = newpoiDbf.getY_coord();
                rowData[7] = newpoiDbf.getType();
                rowData[8] = newpoiDbf.getLevel();
                rowData[9] = newpoiDbf.getBrand();
                rowData[10] = newpoiDbf.getPriority();
                rowData[11] = newpoiDbf.getCategoryNa();
                rowData[12] = newpoiDbf.getCategory();
                rowData[13] = newpoiDbf.getMinScaleID();
                rowData[14] = newpoiDbf.getMaxScaleID();
                rowData[15] = newpoiDbf.getRank();
                rowData[16] = newpoiDbf.getDelete();
                dbfWriter.addRecord(rowData);
            }
            dbfWriter.close();
        }catch (Exception e){

        }
    }
}
