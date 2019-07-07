package com.itheima.search.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.gossip.pojo.News;
import com.itheima.gossip.pojo.ResultBean;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SolrIndexSeacher implements IndexSeacher  {
    @Autowired // 从容器中获取 根据类型获取
    private CloudSolrServer cloudSolrServer;
    @Override
    public ResultBean queryByKeywords(ResultBean resultBean) throws Exception {
        List<News> newsList = new ArrayList<>();
        //1. 封装查询条件
        SolrQuery solrQuery = new SolrQuery("text:"+resultBean.getKeywords());

        //在查询条件基础上再次进行过滤查询，过滤查询不参与打分
        if(!StringUtils.isEmpty(resultBean.getSource())){
            solrQuery.addFilterQuery("source:"+resultBean.getSource());
        }
        if(!StringUtils.isEmpty(resultBean.getEditor())){
            solrQuery.addFilterQuery("editor:"+resultBean.getEditor());
        }

        String startDate = resultBean.getStartDate();
        String endDate = resultBean.getEndDate();//05/07/2019 15:13:36-->MM/dd/yyyy HH:mm:ss转换为 yyyy-MM-dd HH:mm:ss
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)){
           //如果日期不为空
            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            //将字符串转换为日期格式
            Date dateStart = format1.parse(startDate);
            Date dateEnd = format1.parse(endDate);
            //将MM/dd/yyyy HH:mm:ss转换为 yyyy-MM-dd HH:mm:ss
            startDate = format2.format(dateStart);
            endDate = format2.format(dateEnd);
            //过滤查询
            solrQuery.addFilterQuery("time:["+startDate+" TO "+endDate+"]");
        }

        //排序 根据时间降序排序
        solrQuery.setSort(new SolrQuery.SortClause("time",SolrQuery.ORDER.desc));

        //Integer是对象可以为null  int有默认值不能为null
        Integer page = resultBean.getPageBean().getPage();//获取起始页
        Integer pageSize = resultBean.getPageBean().getPageSize();//获取每页条数
        //如果前端没给参数值，那么
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = 15;
        }

        solrQuery.setStart((page - 1)*pageSize);
        solrQuery.setRows(pageSize);


        //设置高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("title");
        solrQuery.addHighlightField("content");
        solrQuery.setHighlightSimplePre("<font color = 'red'>");
        solrQuery.setHighlightSimplePost("</font>");

        //2.执行查询,获取响应对象
        QueryResponse response = cloudSolrServer.query(solrQuery);

        //获取高亮内容
        Map<String, Map<String, List<String>>> map = response.getHighlighting();
        //3. 获取数据 :
        //List<News> newsList = response.getBeans(News.class);在此处是有问题的  ,会有类型转换异常
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SolrDocumentList documents = response.getResults();
        for (SolrDocument document : documents) {

            String id = (String) document.get("id");
            String title = (String) document.get("title");
            String content = (String) document.get("content");
            String docurl = (String) document.get("docurl");
            Date oldTime = (Date) document.get("time");
            String time = simpleDateFormat.format(oldTime);
            String source = (String) document.get("source");
            String editor = (String) document.get("editor");


            Map<String, List<String>> listMap = map.get(id);
            if (listMap != null){
                //获取高亮title
                List<String> list = listMap.get("title");
                if (list != null && list.size() >0){
                    title = list.get(0);
                }

                //获取高亮content
                List<String> contentList = listMap.get("content");
                if (contentList != null && contentList.size() > 0){
                    content = contentList.get(0);
                }

            }

            //如果content数据太长，我们可以截取一小段数据发送给前端
            if(content.length()>200){
               content =  content.substring(0,150)+"....";
            }

            News news = new News();
            news.setId(id);
            news.setTitle(title);
            news.setContent(content);
            news.setDocurl(docurl);
            news.setTime(time);
            news.setSource(source);
            news.setEditor(editor);

            newsList.add(news);

        }
        resultBean.getPageBean().setNewsList(newsList);
        //总记录数
        long pageCount = documents.getNumFound();
        resultBean.getPageBean().setPageCount((int) pageCount);
        //总页数
        double pageNum = Math.ceil((double) pageCount / pageSize);//向上取整
        resultBean.getPageBean().setPageNum((int) pageNum);
        return resultBean;
    }
}
