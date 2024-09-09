# OOMALL

2022 Tutorial Project for course "Object-Oriented Analysis and Design“ and "JavaEE Platform Technologies"

<p>2022-11-03：[API 1.2.0版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.0#/) 2022年第一版API
<p>2022-11-09：[API 1.2.1版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.1#/) 2022年第二版API
<p>2022-11-21：[API 1.2.2版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.2#/) 2022年第三版API

## 单元测试结果
<p>系统每4个小时会自动进行一次单元测试，这是最近一次[测试结果](http://121.36.2.235/unit-test/latest/)和[历史测试结果](http://121.36.2.235/unit-test/)

## 工程编译，调试的顺序
<p>所有module都依赖于core模块，先要将core安装到maven的本地仓库，才能编译运行其他模块。方法如下：
<p>1. 首先将oomall下的pom.xml文件中除·<module>core</module>·以外的module注释掉，
<p>2. 在maven的中跑install phase，将core和oomall安装到maven的本地仓库
<p>3. 将oomall下的pom.xml文件中注释掉的部分修改回来
<p>4. 编译打包其他部分
<p>5. 以后修改了core的代码，只需要单独install core到maven本地仓库，无需重复上述步骤

## Dao层的find和retrieve方法命名
<p>参考 Spring JPA的命名规范
<table><thead><tr><th style="text-align:left"><div><div class="table-header"><p>Keyword</p></div></div></th><th style="text-align:left"><div><div class="table-header"><p>Sample</p></div></div></th><th style="text-align:left"><div><div class="table-header"><p>JPQL snippet</p></div></div></th></tr></thead><tbody><tr><td style="text-align:left"><div><div class="table-cell"><p>And</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByLastnameAndFirstname</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.lastname = ?1 and x.firstname = ?2</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>Or</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByLastnameOrFirstname</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.lastname = ?1 or x.firstname = ?2</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>Is,Equals</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByFirstname,
findByFirstnameIs,
findByFirstnameEquals</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.firstname = ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>Between</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByStartDateBetween</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.startDate between ?1 and ?2</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>LessThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeLessThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age &lt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>LessThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeLessThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age &lt;= ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>GreaterThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeGreaterThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age &gt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>GreaterThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeGreaterThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age &gt;= ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>After</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByStartDateAfter</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.startDate &gt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>Before</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByStartDateBefore</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.startDate &lt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>IsNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeIsNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age is null</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>IsNotNull,NotNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAge(Is)NotNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age not null</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>Like</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByFirstnameLike</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.firstname like ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>NotLike</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByFirstnameNotLike</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.firstname not like ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>StartingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByFirstnameStartingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.firstname like ?1(parameter bound with appended&nbsp;%)</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>EndingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByFirstnameEndingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.firstname like ?1(parameter bound with prepended&nbsp;%)</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>Containing</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByFirstnameContaining</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.firstname like ?1(parameter bound wrapped in&nbsp;%)</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>OrderBy</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeOrderByLastnameDesc</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age = ?1 order by x.lastname desc</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>Not</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByLastnameNot</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.lastname &lt;&gt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>In</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeIn(Collection&lt;Age&gt; ages)</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age in ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>NotIn</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByAgeNotIn(Collection&lt;Age&gt; ages)</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.age not in ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>True</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByActiveTrue()</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.active = true</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>False</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByActiveFalse()</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where x.active = false</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><p>IgnoreCase</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>findByFirstnameIgnoreCase</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><p>… where UPPER(x.firstame) = UPPER(?1)</p></div></div></td></tr></tbody></table>

