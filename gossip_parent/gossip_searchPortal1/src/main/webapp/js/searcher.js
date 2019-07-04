//发送ajax
//1.获取URL上的数据  keywords
var vk = window.location.search;
var v = vk.split("=")[1];
if (v == null || v.trim() == '' || v == undefined) {
    //没有传递的参数就跳转到搜索页面
    location.href = "index.html";
}

//url对中文进行编码  所以我们需要解码
var v = decodeURI(v);


    v = v.replace(/\+/g,"");//将+替换为空
    v = v.replace(/%2B/g,"+");//将%2B替换为加号
//将数据回显到搜索框
$(".inputSeach").val(v);
//发送异步请求
ajaxQuery(1, 15);

//发送异步请求的方法
function ajaxQuery(page, pageSize) {
    $(".itemList").html('');
    //封装过滤条件
    var startDate = $("[name = 'dateStart']").val();
    var endDate = $("[name = 'dateEnd']").val();
    var editor = $("[name='editor']").val();
    var source = $("[name='source']").val();


    var param = {
        'keywords': v,
        'startDate': startDate,
        'endDate': endDate,
        'editor': editor,
        'source': source,
        'pageBean.page': page, 'pageBean.pageSize': pageSize
    };
    $.post('/s.action', param, function (data) {
        //循环dom对象
        $(data.pageBean.newsList).each(function () {

            var strUrl = this.docurl;
            if (strUrl.length > 20) {
                strUrl = strUrl.substring(0, 15) + ".....";
            }

            var html = "\t\t\t<div class=\"item\">\n" +
                "\t\t\t\t<div class=\"title\"><a href=\"#\">" + this.title + "</a></div>\n" +
                "\t\t\t\t<div class=\"contentInfo_src\">\n" +
                "\t\t\t\t\t<a href=\"#\"><img src=\"./img/item.jpeg\" alt=\"\" class=\"imgSrc\" width=\"121px\" height=\"75px\"></a>\n" +
                "\t\t\t\t\t<div class=\"infoBox\">\n" +
                "\t\t\t\t\t\t<p class=\"describe\">\n" +
                "\t\t\t\t\t\t\t" + this.content + "\n" +
                "\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t<p><a class=\"showurl\" href=\"" + this.docurl + "\">" + strUrl + " " + this.time + "</a> <span class=\"lab\">" + this.editor + " - " + this.source + "</span></p>\n" +
                "\t\t\t\t\t</div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n"
            // 将新闻数据填充到页面中
            $(".itemList").append(html);
        })
        //找准位置，上面是新闻内容展示，下面是页码的动态展示
        $(".pageList").html("");//清空上次生成的工具条
        //循环的起始位置
        var start = 1;
        //循环的结束位置，默认是总页数
        var end = data.pageBean.pageNum;

        if(end >7){//当总页数大于7的时候我们才循环
            if(page < 4){//显示前7页
                end = 7;
            }else if(page >= (end -3)){//显示后7页
                start = end - 6;
            }else{//展示前3后3
                start = page - 3;
                end = page + 3;
            }
        }
        var pageStr = "";
        /**
         * <ul>
         <li><a href="#">< 上一页</a></li>
         <li>1</li>
         <li>2</li>
         <li class="on">3</li>
         <li>4</li>
         <li>5</li>
         <li>6</li>
         <li>7</li>
         <li>下一页 ></li>
         </ul>
         */
        pageStr += "<ul>";
        //如果当前页不是第一页，就显示前一页
        if(page != 1){
            pageStr += "<li><a href=\"javascript:void(0)\" onclick='ajaxQuery("+(page - 1)+",15)'>< 上一页</a></li>"
        }

        for(var i = start; i <= end; i++){
            if( i == page){
                pageStr += "<li class='on'>"+i+"</li>"
            }else{

                pageStr += "<li  onclick='ajaxQuery("+i+",15)'>"+i+"</li>"
            }
        }


        if(page != end){
            pageStr += "<li><a href=\"javascipt:void(0)r\" onclick='ajaxQuery("+(page + 1)+",15)'>下一页 ></a></li>";
        }
        //如果不是最后一页，就显示下一页
        pageStr += "</ul>";

        $(".pageList").html(pageStr);

    }, 'json')
}


function ajaxTopKey(num) {
    $.post('/top.action',{'num':num},function (data){
        $(".recommend").html('');
        $(data).each(function () {
            var divStr = " <div class=\"item\" onclick='topAjax(this)'><span>"+this.toKey+"</span>"+"<span style='float: right;color: red'>"+this.score+"</span></div>"
            $(".recommend").append(divStr);
        })
    },'json')
}

function topAjax(obj) {
    var keywords = $(obj).children(":first").text();
    window.location.href = "/list.html?keywords="+keywords;
}
