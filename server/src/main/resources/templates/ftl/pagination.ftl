<#macro pagination value url="list.html" >
    <#if !value?is_sequence>
        <form id="tableForm">
            <input type="text" name="pageNo" value="${value.pageNum}" hidden/>
            <input type="text" name="pageSize" value="${value.pageSize}" hidden/>
            <input type="text" name="keyWord"  class="internal-Keyword" value="${value.pageSize}" hidden/>
            <div class="row">
                <div class="col-md-12 col-sm-12 text-center">
                    共 ${value.total} 条&nbsp;

                    <input class="btn btn-primary radius <#if value.isFirstPage> disabled </#if>" type="button"
                           value="首 页"
                           onclick="_gotoPage('1');"/>

                    <input class="btn btn-primary radius <#if value.isFirstPage> disabled </#if>" type="button"
                           value="上一页"
                           onclick="_gotoPage('${value.prePage}');"/>

                    <input class="btn btn-primary radius <#if value.isLastPage> disabled </#if>" type="button"
                           value="下一页"
                           onclick="_gotoPage('${value.nextPage}');"/>

                    <input class="btn btn-primary radius <#if value.isLastPage> disabled </#if>" type="button"
                           value="尾 页"
                           onclick="_gotoPage('${value.pages}');"/>&nbsp;

                    当前 ${value.pageNum}/${value.pages} 页
                </div>
            </div>
        </form>

        <script type="text/javascript">
            function _gotoPage(pageNo) {
                try {
                    var tableForm = document.getElementById('tableForm');
                    $("input[name='pageNo']").val(pageNo);
                    $(".internal-Keyword").val($("input[name='keyWord']").val());
                    tableForm.action = "${url}";
                    tableForm.onsubmit = null;
                    tableForm.submit();
                } catch (e) {
                    alert('_gotoPage(pageNo)方法出错');
                }
            }
        </script>
    </#if>
</#macro>