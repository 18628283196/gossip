package com.itheima.gossip.controller;

import com.itheima.gossip.pojo.News;
import com.itheima.gossip.pojo.PageBean;
import com.itheima.gossip.pojo.ResultBean;
import com.itheima.gossip.service.IndexSeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**@RestController包含以下注解：
 * @Target(ElementType.TYPE)
 @Retention(RetentionPolicy.RUNTIME)
 @Documented
 @Controller
 @ResponseBody
 */
@RestController
public class IndexSearcherController {

    @Autowired
    private IndexSeacherService indexSeacherService;
    @RequestMapping("/s")
    public ResultBean queryByKeywords(ResultBean resultBean){
      if(resultBean == null){
          return null;
      }
      if (resultBean.getPageBean() == null){
          //如果前端传递的参数包含分页的内容，
          // 会封装一个pagebean对象，但是现在每页分页参数，
          // 就每页PageBean对象，这时候就没法调用PageBean，
          // 需要我们手动new一个对象
          resultBean.setPageBean(new PageBean());
      }

        String keywords = resultBean.getKeywords();
        try {
           keywords = URLDecoder.decode(keywords, "utf-8");
           resultBean.setKeywords(keywords);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        // return (str == null || "".equals(str));
        if (StringUtils.isEmpty(resultBean.getKeywords())){
            return null;
        }
        try {
            return  indexSeacherService.queryByKeywords(resultBean);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
