import bean.Dilution;
import com.vividsolutions.jts.geom.*;
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

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Title: poiDilution
 * @Description: poi点抽稀
 * @Company: www.leador.com.cn
 * @Date: 2019-07-24 14:20
 * @author: wangjiyuan
 * @Version: 1.0
 */

public class poiDilution {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("请输入需要抽稀的shp文件的路径:");
        String inShpPath = input.next();
        System.out.println("请输入该文件的编码(GBK/UTF-8):");
        String inShpCharset = input.next();
        System.out.println("请输入抽稀的距离(米):");
        String dilutionDistance = input.next();
        System.out.println("请输入抽稀完成新的shp文件的路径:");
        String outShpPath = input.next();
        System.out.println("请输入新的shp文件的编码(GBK/UTF-8):");
        String outShpCharset = input.next();
        System.out.println("是否直接删除点(是/否):");
        String isDelete = input.next();
        /*String inShpPath = "E:\\WJY\\点抽稀处理工具\\New_poi_cms.shp";
        String inShpCharset = "utf-8";
        String dilutionDistance = "20";
        String outShpPath = "E:\\WJY\\点抽稀处理工具\\Dilution_poi_cms.shp";
        String outShpCharset = "UTF-8";
        String isDelete = "是";*/
        Double RADIUS = m2Degree(Double.parseDouble(dilutionDistance))*3600;
        boolean isDe = false;
        if (isDelete.equals("是")){
            isDe = true;
        }else if (isDelete.equals("否")){
            isDe = false;
        }
        dilutionOperate(inShpPath,inShpCharset,RADIUS,outShpPath,outShpCharset,isDe);
    }

    public static GeometryFactory geometryFactory = new GeometryFactory();

    public static void dilutionOperate(String inShpPath,String inShpCharset,Double RADIUS,String outShpPath,String outShpCharset,boolean isDe){
        try {
            //读取shp文件
            File file = new File(inShpPath);
            ShapefileDataStore shapefileDataStore = null;
            shapefileDataStore = new ShapefileDataStore(file.toURI().toURL());
            Charset charsetr = Charset.forName(inShpCharset);
            Charset charsetw = Charset.forName(outShpCharset);
            shapefileDataStore.setCharset(charsetr);
            String typeName = shapefileDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = null;
            featureSource = shapefileDataStore.getFeatureSource(typeName);
            SimpleFeatureCollection result = featureSource.getFeatures();
            SimpleFeatureIterator itertor = result.features();
            //所有表记录的集合对象
            List<Dilution> dilutionList = new ArrayList<>();
            while (itertor.hasNext()){
                SimpleFeature feature = itertor.next();
                Dilution dilution = new Dilution();
                dilution.setThe_geom((Geometry) feature.getAttribute("the_geom"));
                if (feature.getAttribute("POIID") == null || feature.getAttribute("POIID").equals("")){
                    dilution.setPOIID("");
                }else {
                    dilution.setPOIID(feature.getAttribute("POIID").toString());
                }
                if (feature.getAttribute("GUIID") == null || feature.getAttribute("GUIID").equals("")){
                    dilution.setGUIID("");
                }else {
                    dilution.setGUIID(feature.getAttribute("GUIID").toString());
                }
                if (feature.getAttribute("Name_chn") == null || feature.getAttribute("Name_chn").equals("")){
                    dilution.setName_chn("");
                }else {
                    dilution.setName_chn(feature.getAttribute("Name_chn").toString());
                }
                if (feature.getAttribute("Name_trd") == null || feature.getAttribute("Name_trd").equals("")){
                    dilution.setName_trd("");
                }else {
                    dilution.setName_trd(feature.getAttribute("Name_trd").toString());
                }
                if (feature.getAttribute("Name_eng") == null || feature.getAttribute("Name_eng").equals("")){
                    dilution.setName_eng("");
                }else {
                    dilution.setName_eng(feature.getAttribute("Name_eng").toString());
                }
                if (feature.getAttribute("X_coord") == null || feature.getAttribute("X_coord").equals("")){
                    dilution.setX_coord("");
                }else {
                    dilution.setX_coord(feature.getAttribute("X_coord").toString());
                }
                if (feature.getAttribute("Y_coord") == null || feature.getAttribute("Y_coord").equals("")){
                    dilution.setY_coord("");
                }else {
                    dilution.setY_coord(feature.getAttribute("Y_coord").toString());
                }
                if (feature.getAttribute("Type") == null || feature.getAttribute("Type").equals("")){
                    dilution.setType("");
                }else {
                    dilution.setType(feature.getAttribute("Type").toString());
                }
                if (feature.getAttribute("Level") == null || feature.getAttribute("Level").equals("")){
                    dilution.setLevel("");
                }else {
                    dilution.setLevel(feature.getAttribute("Level").toString());
                }
                if (feature.getAttribute("Brand") == null || feature.getAttribute("Brand").equals("")){
                    dilution.setBrand("");
                }else {
                    dilution.setBrand(feature.getAttribute("Brand").toString());
                }
                if (feature.getAttribute("priority") == null || feature.getAttribute("Brand").equals("")){
                    dilution.setPriority(0l);
                }else {
                    dilution.setPriority((Long)feature.getAttribute("priority"));
                }
                if (feature.getAttribute("CategoryNa") == null || feature.getAttribute("Brand").equals("")){
                    dilution.setCategoryNa("");
                }else {
                    dilution.setCategoryNa(feature.getAttribute("CategoryNa").toString());
                }
                if (feature.getAttribute("Category") == null || feature.getAttribute("Brand").equals("")){
                    dilution.setCategory("");
                }else {
                    dilution.setCategory(feature.getAttribute("Category").toString());
                }
                if (feature.getAttribute("MinScaleID") == null || feature.getAttribute("Brand").equals("")){
                    dilution.setMinScaleID("");
                }else {
                    dilution.setMinScaleID(feature.getAttribute("MinScaleID").toString());
                }
                if (feature.getAttribute("MaxScaleID") == null || feature.getAttribute("Brand").equals("")){
                    dilution.setMaxScaleID("");
                }else {
                    dilution.setMaxScaleID(feature.getAttribute("MaxScaleID").toString());
                }
                if (feature.getAttribute("Rank") == null || feature.getAttribute("Rank").equals("")){
                    dilution.setRank("");
                }else {
                    dilution.setRank(feature.getAttribute("Rank").toString());
                }
                if (feature.getAttribute("Delete") == null || feature.getAttribute("Delete").equals("")){
                    dilution.setDelete("");
                }else {
                    dilution.setDelete(feature.getAttribute("Delete").toString());
                }
                dilutionList.add(dilution);
            }
            itertor.close();
            //获取画的所有圆的几何集合
            //List<Geometry> circlelist = new ArrayList<>();
            //以priority为key值的treemap,把相同priority的dilution对象放到一个list里面
            TreeMap<Long,List<Dilution>> treemap = new TreeMap<>();
            for (Dilution dilution : dilutionList){
                Geometry shape = dilution.getThe_geom();
                Point point = geometryFactory.createPoint(shape.getCoordinate());
                if (treemap.containsKey(dilution.getPriority())){
                    treemap.get(dilution.getPriority()).add(dilution);
                }else {
                    List<Dilution> dilutions = new ArrayList<>();
                    dilutions.add(dilution);
                    treemap.put(dilution.getPriority(),dilutions);
                }
            }
            //获取Map中的所有key逆序
            Set<Long> keySet = treemap.descendingKeySet();
            //遍历存放所有key的Set集合
            Iterator<Long> iterator =keySet.iterator();
            //从大到小循环处理treemap里面key
            while(iterator.hasNext()){
                Long key = iterator.next();
                List<Dilution> dilutions = treemap.get(key);
                //循环处理key对应的list里面的对象
                for (Dilution dilution : dilutions){
                    System.out.println("------------------开始抽稀<"+dilution.getName_chn()+">"+"Priority值为<"+dilution.getPriority()+">------------------");
                    //如果delete值为1则不处理这个点
                    if (dilution.getDelete().equals("1")){
                        continue;
                    }
                    Geometry shape = dilution.getThe_geom();
                    //以这个点为圆心
                    Point point = geometryFactory.createPoint(shape.getCoordinate());
                    //画出圆面
                    Polygon circle = createCircle(point.getX(),point.getY(),RADIUS);
                    //circlelist.add(circle);
                    //当前中心点的priority值
                    Long thisPriority = dilution.getPriority();
                    //存储priority相同的值的集合
                    List<Dilution> dilutionsp = new ArrayList<>();
                    //测试落在圆内点的个数
                    List<Dilution> dilutiontest = new ArrayList<>();
                    for (Dilution dilution1 : dilutionList){
                        Geometry shape1 = dilution1.getThe_geom();
                        Point pointt = geometryFactory.createPoint(shape1.getCoordinate());
                        boolean isIntersect = circle.intersects(pointt);
                        if (isIntersect && !dilution1.getDelete().equals("1")){
                            if (dilution1.getPriority() < thisPriority){
                                dilutiontest.add(dilution1);
                                dilution1.setDelete("1");
                            }else if (dilution1.getPriority().longValue() == thisPriority){
                                dilutionsp.add(dilution1);
                            }
                        }
                    }
                    if (dilutionsp.size()>1) {
                        //随机取一个保留
                        int index = (int) (Math.random() * (dilutionsp.size()));
                        for (int i = 0; i < dilutionsp.size(); i++) {
                            for (Dilution dilution2 : dilutionList) {
                                //除去这个保留的，其他全部删除
                                if (i==index){
                                    if (dilutionsp.get(index).equals(dilution2)) {
                                        dilution2.setDelete("0");
                                    }
                                }else {
                                    if (dilutionsp.get(i).equals(dilution2)) {
                                        dilution2.setDelete("1");
                                    }
                                }
                            }
                        }
                    }else {
                        for (Dilution dilution2 : dilutionList) {
                            if (dilutionsp.get(0).equals(dilution2)) {
                                dilution2.setDelete("0");
                            }
                        }
                    }
                }
            }
            //导出shp文件,判断是否删除为1的记录
            File fileBuf = new File(outShpPath);
            Map<String, Serializable> params = new HashMap<>();
            params.put(ShapefileDataStoreFactory.URLP.key, fileBuf.toURI().toURL());
            ShapefileDataStore dataStore = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
            //获取原shp文件字段名
            SimpleFeatureType sft = featureSource.getSchema();
            List<AttributeDescriptor> attrs = sft.getAttributeDescriptors();
            //定义图形和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.setCRS(DefaultGeographicCRS.WGS84);
            tb.setName("shapefile");
            for (int i = 0; i < attrs.size(); i++) {
                AttributeDescriptor attr = attrs.get(i);
                tb.add(attr);
            }
            dataStore.createSchema(tb.buildFeatureType());
            //设置编码
            dataStore.setCharset(charsetw);
            //设置Writer，遍历原feature，添加到新feature
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriter(dataStore.getTypeNames()[0], Transaction.AUTO_COMMIT);
            if (isDe){
                for (Dilution dilution : dilutionList) {
                    System.out.println("------------------开始输出shp<" + dilution.getName_chn() + ">------------------");
                    if (dilution.getDelete().equals("0")) {
                        SimpleFeature feature = writer.next();
                        feature.setAttribute("the_geom", dilution.getThe_geom());
                        feature.setAttribute("POIID", dilution.getPOIID());
                        feature.setAttribute("GUIID", dilution.getGUIID());
                        feature.setAttribute("Name_chn", dilution.getName_chn());
                        feature.setAttribute("Name_trd", dilution.getName_trd());
                        feature.setAttribute("Name_eng", dilution.getName_eng());
                        feature.setAttribute("X_coord", dilution.getX_coord());
                        feature.setAttribute("Y_coord", dilution.getY_coord());
                        feature.setAttribute("Type", dilution.getType());
                        feature.setAttribute("Level", dilution.getLevel());
                        feature.setAttribute("Brand", dilution.getBrand());
                        feature.setAttribute("priority", dilution.getPriority());
                        feature.setAttribute("CategoryNa", dilution.getCategoryNa());
                        feature.setAttribute("Category", dilution.getCategory());
                        feature.setAttribute("MinScaleID", dilution.getMinScaleID());
                        feature.setAttribute("MaxScaleID", dilution.getMaxScaleID());
                        feature.setAttribute("Rank", dilution.getRank());
                        feature.setAttribute("Delete", dilution.getDelete());
                    }
                }
            }else {
                for (Dilution dilution : dilutionList) {
                    System.out.println("------------------开始输出shp<" + dilution.getName_chn() + ">------------------");
                    SimpleFeature feature = writer.next();
                    feature.setAttribute("the_geom", dilution.getThe_geom());
                    feature.setAttribute("POIID", dilution.getPOIID());
                    feature.setAttribute("GUIID", dilution.getGUIID());
                    feature.setAttribute("Name_chn", dilution.getName_chn());
                    feature.setAttribute("Name_trd", dilution.getName_trd());
                    feature.setAttribute("Name_eng", dilution.getName_eng());
                    feature.setAttribute("X_coord", dilution.getX_coord());
                    feature.setAttribute("Y_coord", dilution.getY_coord());
                    feature.setAttribute("Type", dilution.getType());
                    feature.setAttribute("Level", dilution.getLevel());
                    feature.setAttribute("Brand", dilution.getBrand());
                    feature.setAttribute("priority", dilution.getPriority());
                    feature.setAttribute("CategoryNa", dilution.getCategoryNa());
                    feature.setAttribute("Category", dilution.getCategory());
                    feature.setAttribute("MinScaleID", dilution.getMinScaleID());
                    feature.setAttribute("MaxScaleID", dilution.getMaxScaleID());
                    feature.setAttribute("Rank", dilution.getRank());
                    feature.setAttribute("Delete", dilution.getDelete());
                }
            }
            writer.write();
            writer.close();
            System.out.println("------------------------抽稀输出完成--------------------------");
            //创建shp文件
            /*File filecircle = new File("E:\\WJY\\点抽稀处理工具\\四川省测试\\Circle.shp");
            Map<String, Serializable> paramc = new HashMap<>();
            paramc.put(ShapefileDataStoreFactory.URLP.key, filecircle.toURI().toURL());
            ShapefileDataStore dataStorec = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(paramc);
            //定义图形和属性信息
            SimpleFeatureTypeBuilder tbc = new SimpleFeatureTypeBuilder();
            tbc.setCRS(DefaultGeographicCRS.WGS84);
            tbc.setName("shapefile");
            tbc.add("the_geom",MultiPolygon.class);
            dataStorec.createSchema(tbc.buildFeatureType());
            //设置编码
            dataStorec.setCharset(charsetw);
            //设置Writer，遍历原feature，添加到新feature
            FeatureWriter<SimpleFeatureType, SimpleFeature> writerc = dataStorec.getFeatureWriter(dataStorec.getTypeNames()[0], Transaction.AUTO_COMMIT);
            for (Geometry geometrycir : circlelist){
                SimpleFeature feature = writerc.next();
                feature.setAttribute("the_geom",geometrycir);
            }
            writerc.write();
            writerc.close();*/
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    /**
     * <p>Title: createCircle</p>
     * <p>Description:创建圆面 </p>
     * <p>Date: 2019年7月24日 下午23:56:46</p>
     * @param x  圆心坐标x
     * @param y  圆心坐标y
     * @param RADIUS 半径
     * @return
     */
    public static Polygon createCircle(double x, double y, final double RADIUS) {
        final int SIDES = 32; // 确定边数
        Coordinate coords[] = new Coordinate[SIDES + 1];
        for (int i = 0; i < SIDES; i++) {
            double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;
            double dx = Math.cos(angle) * RADIUS;
            double dy = Math.sin(angle) * RADIUS;
            coords[i] = new Coordinate((double) x + dx, (double) y + dy);
        }
        coords[SIDES] = coords[0];
        LinearRing ring = geometryFactory.createLinearRing(coords); // 画一个环线
        Polygon polygon = geometryFactory.createPolygon(ring, null); // 生成一个面
        return polygon;
    }

    /**
     * 将米数转换为度数
     * @param d
     * @return
     */
    public static Double m2Degree(Double d){
        //公式:l(弧长)=degree（圆心角）× π（圆周率）× r（半径）/180
        //转换后的公式：degree（圆心角）=l(弧长) × 180/(π（圆周率）× r（半径）)
        Double l = d/1000;
        Double earthRadius = 6371.393;//地球半径:km
        Double degree = (180/earthRadius/Math.PI)*l;
        return degree;
    }
}


