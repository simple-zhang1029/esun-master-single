package esun.core.utils;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


//导出Excel工具类
public class ExcelUtils {
    private  static Object getFieldValueByName(String fieldName,Object o){
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);    //获取方法名
            Method method = o.getClass().getMethod(getter, new Class[] {});  //获取方法对象
            Object value = method.invoke(o, new Object[] {});    //用invoke调用此对象的get字段方法
            return value;  //返回值
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将list集合转成Excel文件
     * @param list  对象集合
     * @param path  输出路径
     * @return   返回文件路径
     */
    public static String createExcel(List<? extends Object> list, String path){
        String result = "";
        if(list.size()==0||list==null){
            result = "没有对象信息";
        }else{
            Object o = list.get(0);
            Class<? extends Object> clazz = o.getClass();
            String className = clazz.getSimpleName();
            Field[] fields=clazz.getDeclaredFields();    //这里通过反射获取字段数组
            File folder = new File(path);
            if(!folder.exists()){
                folder.mkdirs();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = formatter.format(new Date());
            String name = fileName.concat(".xls");
            WritableWorkbook book = null;
            File file = null;
            try {
                file = new File(path.concat(File.separator).concat(name));
                book = Workbook.createWorkbook(file);  //创建xls文件
                WritableSheet sheet  =  book.createSheet(className,0);
                int i = 0;  //列
                int j = 0;  //行
                for(Field f:fields){
                    j = 0;
                    Label label = new Label(i, j,f.getName());   //这里把字段名称写入excel第一行中
                    sheet.addCell(label);
                    j = 1;
                    for(Object obj:list){
                        Object temp = getFieldValueByName(f.getName(),obj);
                        String strTemp = "";
                        if(temp!=null){
                            strTemp = temp.toString();
                        }
                        sheet.addCell(new Label(i,j,strTemp));  //把每个对象此字段的属性写入这一列excel中
                        j++;
                    }
                    i++;
                }
                book.write();
                result = file.getPath();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                result = "SystemException";
                e.printStackTrace();
            }finally{
                fileName = null;
                name = null;
                folder = null;
                file = null;
                if(book!=null){
                    try {
                        book.close();
                    } catch (WriteException e) {
                        // TODO Auto-generated catch block
                        result = "WriteException";
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        result = "IOException";
                        e.printStackTrace();
                    }
                }
            }

        }

        return result;   //最后输出文件路径
    }

    public static  String createMapListExcel(List<Map<String,Object>> list,String path){
        String result = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = formatter.format(new Date());
        String name = fileName.concat(".xls");
        if(list.size()==0||list==null){
            result = "没有对象信息";
            return result;
        }
        WritableWorkbook book = null;
        File file = null;
        try {
            file = new File(path.concat(File.separator).concat(name));
            book = Workbook.createWorkbook(file);  //创建xls文件
            WritableSheet sheet  =  book.createSheet(name,0);
            //获取Map Key 为表头
            Set set=list.get(0).keySet();
            Iterator iterator=set.iterator();
            int i=0;//行变量
            int j=0;//列变量
            List<String> titleList=new ArrayList<>();
            while (iterator.hasNext()){
                String key=(String) iterator.next();
                Label label=new Label(i,j, key);
                sheet.addCell(label);
                titleList.add(key);
                i++;
            }
            //写入数据
            for ( j = 0; j <list.size() ; j++) {
                for(i=0; i<titleList.size(); i++){
                    String value=list.get(j).get(titleList.get(i)).toString();
                    Label label=new Label(i,j+1,value);
                    sheet.addCell(label);
                }
            }
            book.write();
            result = file.getPath();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            result = "SystemException";
            e.printStackTrace();
        }
        finally{
            fileName = null;
            name = null;
            file = null;
            if(book!=null){
                try {
                    book.close();
                } catch (WriteException e) {
                    // TODO Auto-generated catch block
                    result = "WriteException";
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    result = "IOException";
                    e.printStackTrace();
                }
            }
        }


        return result;
    }

    public static  String createMapListExcel(List<Map<String,Object>> list,String path,List<?> titleList){
        String result = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String fileName = formatter.format(new Date());
        String name = fileName.concat(".xls");
        if(list.size()==0||list==null){
            result = "没有对象信息";
            return result;
        }
        WritableWorkbook book = null;
        File file = null;
        try {
            file = new File(path.concat(File.separator).concat(name));
            book = Workbook.createWorkbook(file);  //创建xls文件
            WritableSheet sheet  =  book.createSheet(name,0);
            //获取Map Key 为表头
            int i=0;//行变量
            int j=0;//列变量
            for (i = 0; i <titleList.size() ; i++) {
                Label label=new Label(i,j, titleList.get(i).toString());
                sheet.addCell(label);
            }
            //写入数据
            for ( j = 0; j <list.size() ; j++) {
                for(i=0; i<titleList.size(); i++){
                    String value=list.get(j).get(titleList.get(i)).toString();
                    Label label=new Label(i,j+1,value);
                    sheet.addCell(label);
                }
            }
            book.write();
            result = file.getPath();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            result = "SystemException";
            e.printStackTrace();
        }
        finally{
            fileName = null;
            name = null;
            file = null;
            if(book!=null){
                try {
                    book.close();
                } catch (WriteException e) {
                    // TODO Auto-generated catch block
                    result = "WriteException";
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    result = "IOException";
                    e.printStackTrace();
                }
            }
        }


        return result;
    }
}
