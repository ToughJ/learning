生成环的子问题

张米硕士论文

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

\pi是主问题对偶得到的机会成本。

x是不过夜的，y是过夜的连接。

e,h是预先的  前级和后继节点。

s是航班i是出发航班，e是终止

4,5,6是三个约束，当日飞行时长约束，总执勤时长约束，过夜日数约束

7是总要有航班安排的意思。

---

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

刚刚那个的改编版，基地机场只有一个了。

\begin{align}
min & \sum_{p\in P} c_px_p
\\
s.t. & \sum_{p\in P}a_{ip}x_p=1
\\
&x_p \in {0,1}
\end{align}

i 代表航班，i\inI，I 为所有待执行航班集合；
t 表示时间周期，t\inT，T 为日期集合；
wi 表示 i 航班的飞行津贴、任务津贴等人工成本；
ri 表示 i 航班的在外过夜的成本；
\pi_i为来自主问题的 i 航班的机会成本（对偶解）；
h(i) 表示 i 航班的前导航班；
b(i) 表示 i 航班的后继航班；
fti  表示 i 航班的飞行时间，指机组成员在飞机飞行期间的工作时间；
tdepi 表示 i 航班的起飞时刻；
tarii 表示 i 航班的降落时刻；
maxh 表示机组每天的最大飞行时间；
maxdh 表示机组每天的最大执勤时间；



---

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

---

---

新规则下

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

将t 设为第一个 第二个 、、、 执勤期

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

---

二阶段

第一阶段生成ToD，第二阶段将生成的ToD排入一个Schedule中

周期性原因，Schedule规定为十四天的，由于ToD是六天周期的，因此Schedule的安排具有弹性，允许一个周期的最后五天是弹性安排的

也就是一个周期的第一天之前还有五天的任务和人员可以安排。

Master Problem

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

q指的出发城市，\varphi_{pq}表示是否schedule p 是q城市处罚的

n_{-5}是周期五天前有空的人数，n_0是周期第一天有空的人数

a,b,c,d是表示第p个schedule是否有i这个段。

a表示上一周期留下的前五天，b是周期的第一周，c是第二周的周一和周二，d是第二周的后五天，可以松弛弹性的部分。

---

Sub-problem

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

把ToD按照开始的天数分为了三段。

第一段：-5 ，-4天开始的ToD

第二段：-3 -2 -1 0 1 2 这六天开始的ToD

第三段： 3 4 5 6 7 8 这六天开始的ToD

9-13开始的ToD都凑不够六天了，下一个周期的内容。

难点在\pi的处理,需要预处理。同样的ToD，如果分到了不同的段里，\pi是不同的，即\pi_{1k} \ne \pi_{2k}

---

二阶段(another edition)

第一阶段生成ToD，第二阶段将生成的ToD排入一个Schedule中

周期性原因，Schedule规定为5+7=12天的，由于ToD是六天周期的，因此Schedule的安排具有弹性，允许一个周期的最后五天是弹性安排的

但是下一次运算的时候，还是从七天后开始运算，也就是两次运算的周期是有重合的地方。

也就是一个周期的第一天到第五天的任务和人员可能已经被安排了。

Master Problem

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

q指的出发城市，\varphi_{pq}表示是否schedule p 是q城市处罚的

n_{0}是周一有空的人数，n_6是周日有空的人数

a,b是表示第p个schedule是否有i这个段。

a表示周期的第一周，b第二周的五天，可以松弛弹性的部分。

---

Sub-problem

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

把ToD按照开始的天数分为了2段。

第一段：周一到周六天开始的ToD

第二段：周日开始的ToD

从下周一开始就凑不够六天了，下一个周期的内容。

难点在\pi的处理,需要预处理。同样的ToD，如果分到了不同的段里，\pi是不同的，即\pi_{1k} \ne \pi_{2k}


