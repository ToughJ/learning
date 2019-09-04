###  1Air Transat Uses ALTITUDE to Manage Its Aircraft Routing, Crew Pairing, and Work Assignment(Desrosiers et al., 2000)

---

**Air Transat faces typical air transport carriers’ routing and crew-scheduling problems**:       

​       which planes to fly to what destinations and what staff member to assign to what route at what time to maximize the return on the company’s assets. 

**第一个**： 航班调度和航班分配阶段首先根据预测的需求、公司在不同机场拥有的时间间隔和竞争情况，确定在给定时间段内飞行的所有航班段或航班腿。

根据市场需求 分配航线（flight leg)  需要根据需求、飞机容量和速度，为每条腿分配了特定的飞机类型



首先，航空公司市场部基于对市场状况的预测，结合航空公司的运营能力现状（如机场跑道长度、空管、航程限制等）和硬件设施限制（如值机柜台、行李处理能力、登机门位置、地面设备等），分别从战术层和战略层制定航班计划。航班计划是航空公司在整个航线网络层上对所有航班进行设计和优化的过程（孙宏, 2008），它是航空公司所有生产经营活动的基础，是整个运输运营计划的核心，飞机排班和机组排班必须围绕航班计划展开。 

完整的航班计划包含航线（可进行航空运输的路线）、航班（包括航线、航班号、起降时刻、起降机场等信息）、班期（某航班在周期内的哪几天被执行）、班次（每天每个航线上有多少航班）、机型（各航班所使用的飞机型号）等信息

---

**第二个 **： 根据航线（flight leg) 分配飞机的使用， （租飞机费用 维修时间  最小连接时间等）

aircraft-routing

最小化 租用额外飞机的费用，根据需求变动调整飞机位置的费用，以及每架飞机在进行维修检查前剩余时数的减少的罚款费用的总和。

, a flight averages three legs,



在完成航班计划的制定后，进行飞机排班计划。这里的飞机排班是包含了飞机维修计划的排班，综合考虑航班计划和飞机维修计划，完成航班任务和具体飞机的匹配的过程，即确定每一个航班由哪架飞机执飞。之所以将飞机维修计划融入到飞机排班计划中，是因为飞机排班计划不仅要保证各航班任务被顺利执行，还要保证飞机在接近维修期限时及时返回维修基地进行维护。航空条例中对于飞机的飞行时间和维修间隔有着严格的限制，以保证飞行的安全性，当飞机到达维修期限时，必须进站维修，因此，对飞机飞行任务的指派需将飞机维修计划考虑其中。

由于每架飞机都有一个被称为尾号的唯一编号，航空业把为飞机指派飞行任务的过程又称为尾号分配（Tail Number Assignment）。合理的飞机排班计划可以有效减少飞机的空调现象，并缩短飞机的地面停场时间，降低飞机的平均飞行成本。



---

**第三阶段**是建立有效的船员配对，也称为船员轮换，以尽量降低船员成本.

crew-pairing

分配 飞行，乘客飞行 简报  休息等活动表

A pairing is a detailed schedule of activities, such as flight legs, deadhead legs (crew members fly as passengers), briefings and debriefings, breaks and nighttime rests, that start and end at the same crew base.

地面运输减少死头

Pairings for the flight-deck personnel differ from those for the flight-attendant personnel 

人文关怀 -> 软规定

minimizes the number of legs at the ends of transatlantic pairings。



在完成航班计划和飞机排班计划制定的基础上，进行机组排班方案的制定。
机组排班即根据航班属性，为航班指派相应的机组人员，以完成航班的飞行作业，其主要目标是机组排班成本的最小化。机组排班部分是本文的主要研究内容，也是整个航空运输运营计划中非常重要和复杂的部分。

机组排班问题理论上可以描述为集合分割问题（Rubin, 1973）。如表 2.1 所示，假设有 m 个待执行航班，有 n 个可行的任务环（“任务环”的定义见附录 A），且每个备选任务环都是合法的（均满足附录 B 中关于任务环的约束）。

![1557037326031](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1557037326031.png)

表中行列交叉处为 1 表示该任务环中包含当前航班，若为空白则表示不包含当前航班（如表中两个圆圈处即表示在第 2 个任务环包含航班 2，但不包含航班 3）。机组排班问题需要从所有可行任务环中，选出成本最小的任务环组合，使所有航班均被覆盖且仅被覆盖一次，即所有航班行向加和一定为 1。

![1557037378608](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1557037378608.png)

![1557037394512](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1557037394512.png)

![1557037467481](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1557037467481.png)

---

**第四阶段**

bidding and rostering problems

将员工按月分配。每个区块都描述了一个月里一名船员的活动

配对、训练时间、预备时间、空白时间、度假以及每月10天的假期

(空白 联系上要报告工作v   预备 随叫随到)

The **input data** required for this phase are crew pairings, labor contract clauses, and crew welfare information.

The process of assigning to each crew member blocks that account for preferences this employee has expressed is called **rostering**.

When blocks are built without regard to crew members’ desires, the process is called **bidding**.

混合rostering  bidding的方法

每个员工类别中的个人都表示出偏好，软件将其视为加权投标，按顺序进行，从最资深的员工开始构建时间表，以最初级的员工结束。对于被考虑的员工，我们确定最佳的，或最大的得分，计划考虑所有剩余员工的加权出价，并为所有已被考虑的员工给出固定的时间表得分

---

![1556278216425](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1556278216425.png)

![1556278226396](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1556278226396.png)



---

##set-partitioning-type model

aircraft-routing problem

crew-pairing problem

bidding and rostering problems

we use a decomposition process based on generating work patterns as needed。

**column-generation methodology embedded in a branch-and-bound algorithm**

![1554601493755](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1554601493755.png)

**Model**

![1554601760981](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1554601760981.png)

J 是  子问题生成的  patterns

aircraft routing ：   $$X^k_j$$ 是第j 种飞机飞行方案就是 航线  k 是飞机尾号    I 是 leg

crew-pairing problem :  $$X^k_j$$  是第j 个 配对方案 k 是 基地  I 是  航线

rostering and biding :   $$X^k_j$$  是第j个安排方案  k 是  特定人员 和人员类型   I 是  日程方案

M  是全局约束。  与i 无关

eg 在飞机航线问题中，这样的约束确保所使用的给定类型的飞机数量不超过可用的数量。在机组配对问题中，它确保从一个基地部署的机组人数不超过可用的人数



---

###[新西兰论文]( https://doi.org/10.1287/inte.31.1.30.9688)(Butchers et al., 2001)中例子

![1556278251524](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1556278251524.png)

![1556278261809](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1556278261809.png)

上面两个是第三阶段的例子，下面的是第四阶段的例子。

![1556278288314](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1556278288314.png)

![1556278297530](C:\Users\zjceladon\AppData\Roaming\Typora\typora-user-images\1556278297530.png)

---

### Air France 法航  论文

---

###11年一篇硕士论文 

例如 1997 年南方航空公司就与美国世博公司（SABER）合作引进了 SOC 系统，2006 年中国国航也同 SABER 公司签署了 SOC项目合同

国际性的研究团体：  国际航空运输协会（IATA,International Air Transport Association），国际运筹学联合会航空工作组（AGIFORS, Airline Group for International Federation of Operation Research）

---

#####生成环的子问题

张米硕士论文
$$
\begin{align}
min \ \ \ \ \ \ \  \ & (w_i - \pi _i)x_{ijt} +(r_i - \pi_i)y_{ijt}\\
s.t. &s_{it}+\sum_{j\in h(i)}x_{jit}+\sum_{j\in h(i)}y_{jit}=
e_{it}+\sum_{j\in e(i)}x_{ijt}+\sum_{j\in e(i)}y_{ijt} \ ,\ \ \forall i,\forall t \tag{1}
\\ 
& \sum _{i,t}s_{it} =1 \tag{2}
\\
& \sum _{i,t}e_{it} =1 \tag{3}
\\
& \sum_{i,j}ft^0_ix_{ijt} + \sum_{i,j}ft^1_iy_{ijt} + \sum_{i,j}ft^2_iy_{jit-1}\le maxh \ ,\ \ \forall t \tag{4}
\\
& \sum_i tari_ie_{it}-\sum_itdep_is_{it}\le maxdh \ \ \forall t \tag{5}
\\
& \sum_{i,j} y_{ijt}\le maxdays \tag{6}
\\
& \sum_{i,j,t}x_{jit}+\sum_{i,j,t}y_{jit}  > 0 , \tag{7} \\

& x_{ijt} ,y_{ijt},s_{it},e_{it} \in \{0,1\} \tag{8}
\end{align}
$$

$\pi$是主问题对偶得到的机会成本。

x是不过夜的，y是过夜的连接。

e,h是预先的  前级和后继节点。

s是航班i是出发航班，e是终止

4,5,6是三个约束，当日飞行时长约束，总执勤时长约束，过夜日数约束

7是总要有航班安排的意思。

---

$$
\begin{align}
min \ \ \ \ \ \ & (w_i - \pi _i)x_{ijt} +(r_i - \pi_i)y_{ijt}\\
s.t.\ \ \ \ \ \ \ \
     & \sum_{j\in h(i)}x_{jit}+  \sum_{j\in h(i)}y_{jit}+
       \sum_{j in h(i)}x^0_{jit}+\sum_{j\in h(i)}y^0_{jit}=\\&
       \sum_{j\in e(i)}x_{ijt}+  \sum_{j\in e(i)}y_{ijt}+
       \sum_{j\in e(i)}x^0_{ijt}+\sum_{j\in e(i)}y^0_{ijt} 
       \ ,\ \ \forall i\ne ori,\forall t \tag{1}
\\
& s_t + \sum_{j\in h(ori)}x_{j(ori)t}+  \sum_{j\in h(ori)}y_{j(ori)t}+
        \sum_{j\in h(ori)}x^0_{j(ori)t}+\sum_{j\in h(ori)}y^0_{j(ori)t}=\\&
e_t+\sum_{j\in e(ori)}x_{(ori)jt}+  \sum_{j\in e(ori)}y_{(ori)jt}+
    \sum_{j\in e(ori)}x^0_{(ori)jt}+\sum_{j\in e(ori)}y^0_{(ori)jt}
\ ,\ \ \forall t \tag{2}
\\
& \sum _{t}s_{t} =1 \tag{3}
\\
& \sum _{t}e_{t} =1 \tag{4}
\\
& \sum_{i,j}ft^0_ix_{ijt} + \sum_{i,j}ft^1_iy_{ijt} + \sum_{i,j}ft^2_iy_{jit-1}\le maxh \ ,\ \ \forall t \tag{5}
\\
& \sum_i tari_ie_{t}-\sum_itdep_is_{t}\le maxdh \ \ \forall t \tag{6}
\\
& \sum_{t\in (1,tt)} s_t \ge \sum_{tt\in (1,tt)} e_t \ \ , \forall tt \tag{7}\\
& \sum_{i,j} y_{ijt}\le maxdays \tag{8}
\\
& \sum_{i,j,t}x_{jit}+\sum_{i,j,t}y_{jit}  > 0 , \tag{9} \\
& x_{ijt} ,y_{ijt},s_{it},e_{it} \in \{0,1\} \tag{10}
\end{align}
$$

刚刚那个的改编版，基地机场只有一个了。
$$
\begin{align}
min & \sum_{p\in P} c_px_p
\\
s.t. & \sum_{p\in P}a_{ip}x_p=1
\\
&x_p \in {0,1}
\end{align}
$$
i 代表航班，i$\in$I，I 为所有待执行航班集合；
t 表示时间周期，t$\in$T，T 为日期集合；
wi 表示 i 航班的飞行津贴、任务津贴等人工成本；
ri 表示 i 航班的在外过夜的成本；
$\pi_i$为来自主问题的 i 航班的机会成本（对偶解）；
h(i) 表示 i 航班的前导航班；
b(i) 表示 i 航班的后继航班；
fti  表示 i 航班的飞行时间，指机组成员在飞机飞行期间的工作时间；
tdepi 表示 i 航班的起飞时刻；
tarii 表示 i 航班的降落时刻；
maxh 表示机组每天的最大飞行时间；
maxdh 表示机组每天的最大执勤时间；



---

$$
\begin{align}min \ \ \ \ \ \ \  \ & (w_i - \pi _i)x_{ijt} +(r_i - \pi_i)y_{ijt}\\s.t. &s_{it}+\sum_{j\in h^0(i)}x_{jit}+\sum_{j\in h^1(i)}y_{jit-1}=e_{it}+\sum_{j\in b^0(i)}x_{ijt}+\sum_{j\in b^1(i)}y_{ijt} \ ,\ \ \forall i,\forall t \tag{1}\\ 
& \sum _{i,t}s_{it} =1 \tag{2}\\
& \sum _{i,t}e_{it} =1 \tag{3}\\
& \sum_{i,j}ft_ix_{ijt} + \sum_{i,j}ft_iy_{ijt} \le maxh \ ,\ \ \forall t \tag{4}\\
& 0\le\sum_i\sum_t t^{Ari}_ie_{it}-\sum_i\sum_tt^{Dep}_is_{it}\le maxdh \ \  \tag{5}\\
& \sum_{i,j} y_{ijt}\le maxdays \tag{6}\\
& s_{it}\le BL_{it} \tag{7}\\
& e_{it}\le BL_{it} \tag{8}\\
& \sum_{i,t}s_{it}Dep_i = \sum_{i,t}e_{it}Arr_i \tag{9}\\
& x_{ijt} \le XL_{ijt} \tag{10}\\
& y_{ijt} \le YL_{ijt} \tag{11}\\
& \sum_{j,t} (x_{ijt} + y_{ijt}) \le 1 \tag{12}\\
& x_{ijt} ,y_{ijt},s_{it},e_{it} \in \{0,1\} \tag{13}
\end{align}
$$

---

-----

**新规则下**
$$
\begin{align}max \ \ \ \ \ \ \  \ & (1 - \pi _i)x_{ijt} +(1 - \pi_i)y_{ijt} + (1-\pi_i) e_{it}
\\s.t. &s_{it}+\sum_{j\in h^0(i)}x_{jit}+\sum_{j\in h^1(i)}y_{jit-1}=e_{it}+\sum_{j\in b^0(i)}x_{ijt}+\sum_{j\in b^1(i)}y_{ijt} \ ,\ \ \forall i,\forall t \tag{1}
\\ & \sum _{i,t}s_{it} =1 \tag{2}
\\& \sum _{i,t}e_{it} =1 \tag{3}
\\& \sum_{i,j}ft_ix_{ijt} + \sum_{i,j}ft_iy_{ijt} + \sum_{i}ft_ie_{it} \le maxh \ ,\ \ \forall t \tag{4}
\\& \sum_i\sum_jy_{ijt}t^{Ari}_i - \sum_is_{it}t^{Dep}_i \le maxhours + (1-\sum_is_{it})M, \ \ \forall t \ \  \tag{5}
\\& \sum_i\sum_jy_{ijt}t^{Ari}_i - \sum_i\sum_jy_{ijt-1}t^{Dep}_i \le maxhours + (\sum_ie_{it}+\sum_is_{it})M, \ \ \forall t \ \  \tag{6}
\\& \sum_ie_{it}t^{Ari}_i - \sum_i\sum_jy_{ijt-1}t^{Dep}_i \le maxhours + (1-\sum_ie_{it})M, \ \ \forall t \ \  \tag{7}
\\& \sum_{i,j} y_{ijt}\le maxdays \tag{8}
\\& s_{it}\le BL_{it} \tag{9}
\\& e_{it}\le BL_{it} \tag{10}
\\& \sum_{i,t}s_{it}Dep_i = \sum_{i,t}e_{it}Arr_i \tag{11}
\\& x_{ijt} \le XL_{ijt} \tag{12}
\\& y_{ijt} \le YL_{ijt} \tag{13}
\\& \sum_{j,t} (x_{ijt} + y_{ijt}) \le 1 \tag{14}
\\& x_{ijt} ,y_{ijt},s_{it},e_{it} \in \{0,1\} \tag{15}
\end{align}
$$

将t 设为第一个 第二个 、、、 执勤期
$$
\begin{align}max \ \ \ \ \ \ \  \ & (1 - \pi _i)x_{ijt} +(1 - \pi_i)y_{ijt} + (1-\pi_i) e_{it}
\\s.t. &s_{i}+\sum_{j\in h^0(i)}x_{jit}+\sum_{j\in h^1(i)}y_{jit-1}=e_{it}+\sum_{j\in b^0(i)}x_{ijt}+\sum_{j\in b^1(i)}y_{ijt} \ ,\ \ \forall i,\forall t \tag{1}
\\ & s_{i}+\sum_{j\in h^0(i)}x_{jit}+\sum_{j\in h^1(i)}y_{jit-1}\le 1 \tag{13}
\\ & \sum _{i}s_{i} =1 \tag{2}
\\& \sum _{i,t}e_{it} =1 \tag{3}
\\& \sum_{i,j}ft_ix_{ijt} + \sum_{i,j}ft_iy_{ijt} + \sum_{i}ft_ie_{it} \le maxh \ ,\ \ \forall t \tag{4}
\\& \sum_ie_{i0}t^{Ari}_i+\sum_i\sum_jy_{ij0}t^{Ari}_i - \sum_is_{i}t^{Dep}_i  \le maxhours+(p^1_0-1)*weekmin , \ \  \ \  \tag{5}
\\& \sum_ie_{i0}t^{Ari}_i+\sum_i\sum_jy_{ij0}t^{Ari}_i - \sum_is_{i}t^{Dep}_i  \ge 0 +(p^1_0-1)*weekmin , \ \  \ \  \tag{5}
\\& \sum_i\sum_jy_{ijt}t^{Ari}_i+\sum_ie_{it}t^{Ari}_i - \sum_i\sum_jy_{ijt-1}t^{Dep}_j  \le maxhours  + (p^1_t-1)*weekmin , \ \ \forall t>0 \ \  \tag{6}
\\& \sum_i\sum_jy_{ijt}t^{Ari}_i+\sum_ie_{it}t^{Ari}_i - \sum_i\sum_jy_{ijt-1}t^{Dep}_j  \ge 0 + (p^1_t-1)*weekmin , \ \ \forall t>0 \ \  \tag{6}
\\& \sum_i\sum_te_{it}t^{Ari}_i - \sum_i s_i t^{Dep}_i \le allDutyMin + (p^2-1)*weekmin \tag{7}
\\& \sum_i\sum_te_{it}t^{Ari}_i - \sum_i s_i t^{Dep}_i \ge 0  + (p^2-1)*weekmin\tag{7}
\\& \sum_i\sum_jy_{ijt} \le \sum_i\sum_jy_{ijt-1}  \ \ t=1,2\tag{9}\\
\\& \sum_i\sum_jy_{ijt} = \sum_{k=3..t+1}\sum_ie_{ik}   \ \ \forall t=0,1,2\tag{10}\\
\\& \sum_{i,j} y_{ijt}\le maxdays \tag{11}\\& \sum_{i,t}s_{it}Dep_i = \sum_{i,t}e_{it}Arr_i \tag{12}
\\& x_{ijt} ,y_{ijt},s_{it},e_{it} \in \{0,1\} \tag{13}
\end{align}
$$

---

####二阶段

第一阶段生成ToD，第二阶段将生成的ToD排入一个Schedule中

周期性原因，Schedule规定为十四天的，由于ToD是六天周期的，因此Schedule的安排具有弹性，允许一个周期的最后五天是弹性安排的

也就是一个周期的第一天之前还有五天的任务和人员可以安排。

Master Problem
$$
\begin{align}
min & \sum_{p\in P} z_p + w_1\sum_{i \in lastFiveDays}s_i
\\
s.t. & \sum_{p\in P}\varphi_{pq} z_{p}<=n_{-5q}  ,if\ startDay=3,transWeek=2, \forall q
\\
& \sum_{p\in P}\varphi_{pq}z_{p}<=n_{-5q}+n_{-4q}  ,if\ startDay=3\&4,transWeek=2, \forall q
\\  &...   \\
& \sum_{p\in P}\varphi_{pq}z_{p}<=n_{-5q}+\dots+n_{0q}  ,if\ startDay=3-7-1,transWeek =1, \forall q
\\
&\sum_pa_{pi}z_p\ge 1 
\\
&\sum_pb_{pi}z_p\ge 1 
\\
&\sum_pc_{pi}z_p\ge 1 
\\
&\sum_pd_{pi}z_p\ge 1 - s_i
\\
&x_p \in {0,1}
\end{align}
$$
q指的出发城市，$\varphi_{pq}$表示是否schedule p 是q城市处罚的

$n_{-5}$是周期五天前有空的人数，$n_0$是周期第一天有空的人数

a,b,c,d是表示第p个schedule是否有$i$这个段。

a表示上一周期留下的前五天，b是周期的第一周，c是第二周的周一和周二，d是第二周的后五天，可以松弛弹性的部分。

---

Sub-problem
$$
\begin{align}
max & \sum_{o=1,2,3}\sum_{k\in K}\pi_{o,k}y_{o,k}
\\
s.t.\\
& \sum_ky_{2k}StartTime -\sum_ky_{1k}EndTime\ge48*60+1.5*60
\\
& \sum_ky_{3k}StartTime -\sum_ky_{2k}EndTime\ge48*60+1.5*60
\\
&\sum_ky_{1k}City = \sum_ky_{2k}City = \sum_ky_{3k}City
\\
&\sum_ky_{o,k}\le1,  \forall o=1,2,3
\\
&y_{o,k} \in {0,1}
\end{align}
$$
把ToD按照开始的天数分为了三段。

第一段：-5 ，-4天开始的ToD

第二段：-3 -2 -1 0 1 2 这六天开始的ToD

第三段： 3 4 5 6 7 8 这六天开始的ToD

9-13开始的ToD都凑不够六天了，下一个周期的内容。

**难点**在$\pi$的处理,需要预处理。同样的ToD，如果分到了不同的段里，$\pi$是不同的，即$$\pi_{1k} \ne \pi_{2k}$$

----

#### 二阶段(another edition)

第一阶段生成ToD，第二阶段将生成的ToD排入一个Schedule中

周期性原因，Schedule规定为5+7=12天的，由于ToD是六天周期的，因此Schedule的安排具有弹性，允许一个周期的最后五天是弹性安排的

但是下一次运算的时候，还是从七天后开始运算，也就是两次运算的周期是有重合的地方。

也就是一个周期的第一天到第五天的任务和人员可能已经被安排了。

Master Problem
$$
\begin{align}
min & \sum_{p\in P} z_p + w_1\sum_{i \in lastFiveDays}s_i
\\
s.t. & \sum_{p\in P}\varphi_{pq} z_{p}<=n_{0q}  ,if\ startDay=0, \forall q
\\
& \sum_{p\in P}\varphi_{pq}z_{p}<=n_{0q}+n_{1q}  ,if\ startDay=0\&1,\forall q
\\  &...   \\
& \sum_{p\in P}\varphi_{pq}z_{p}<=n_{0q}+\dots+n_{6q}  ,if\ startDay=0-6,\forall q
\\
&\sum_pa_{pi}z_p\ge 1 
\\
&\sum_pb_{pi}z_p\ge 1 - s_i
\\
&x_p \in {0,1}
\end{align}
$$
q指的出发城市，$\varphi_{pq}$表示是否schedule p 是q城市处罚的

$n_{0}$是周一有空的人数，$n_6$是周日有空的人数

a,b是表示第p个schedule是否有$i$这个段。

a表示周期的第一周，b第二周的五天，可以松弛弹性的部分。

------

Sub-problem
$$
\begin{align}
max & \sum_{o=1,2,3}\sum_{k\in K}\pi_{o,k}y_{o,k}
\\
s.t.\\
& \sum_ky_{2k}StartTime -\sum_ky_{1k}EndTime\ge48*60+1.5*60
\\
&\sum_ky_{1k}City = \sum_ky_{2k}City
\\
&\sum_ky_{o,k}\le1,  \forall o=1,2
\\
&y_{o,k} \in {0,1}
\end{align}
$$
把ToD按照开始的天数分为了2段。

第一段：周一到周六天开始的ToD

第二段：周日开始的ToD

从下周一开始就凑不够六天了，下一个周期的内容。

**难点**在$\pi$的处理,需要预处理。同样的ToD，如果分到了不同的段里，$\pi$是不同的，即$$\pi_{1k} \ne \pi_{2k}$$

