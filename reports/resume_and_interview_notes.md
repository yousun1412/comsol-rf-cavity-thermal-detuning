# Resume And Interview Notes

## Resume Bullets In Chinese

```latex
\datedsubsection{\textbf{基于 COMSOL 的 RF 腔多物理场分阶段验证与热失谐分析}}{2026.06}
\begin{itemize}
  \item 面向加速器 RF 腔在高频电磁场作用下的壁面发热、热膨胀与谐振频率漂移问题，搭建 RF 本征频率、独立热学、独立结构热膨胀、RF-to-thermal、thermal-to-structural 与 thermal detuning 的分阶段验证流程；通过复现官方 RF 腔 benchmark，将本征频率误差控制在 $10^{-4}\%$ 量级以内，并结合热平衡、位移量级和参数单调性检查仿真链路可靠性。
  \item 基于 RF 磁场幅值与铜表面电阻推导 field-derived wall loss，将壁面损耗映射为热通量边界，并将稳态温度场传递至结构模块计算热膨胀；进一步采用等效参数化几何回灌 RF 本征频率模型，获得热失谐方向与量级。
  \item 针对最大热负载工况开展 coarse/normal/fine 三档 RF 网格敏感性分析，频偏结果随网格变化低于 $0.003\%$，验证当前热失谐结论主要受等效几何回灌假设影响，而非 RF 网格分辨率主导。
\end{itemize}
```

## One-Minute Interview Version

这个项目是一个 RF 腔热失谐的 COMSOL 分阶段验证。我没有一开始就做一个黑盒式多物理场耦合，而是先复现官方 RF 本征频率 benchmark，确认电磁模型可信；再分别做独立热学和独立结构热膨胀，检查热平衡、位移量级和参数趋势。

之后我用 RF 磁场幅值和铜表面电阻推导壁面损耗热源，把 wall loss 映射成热通量，求出温度场，再把温度场传给结构模块计算热膨胀。最后用等效几何参数把热膨胀反馈回 RF 本征频率模型，得到 hot frequency 和 cold frequency 的差值，也就是 thermal detuning。

这个项目的重点不是说我做了一个工业级全闭环求解，而是我把电磁、热、结构、热失谐每一步都做了验证。最后还做了 coarse/normal/fine 三档 RF 网格敏感性，证明当前频偏不是网格粗细导致的。

## Three-Minute Interview Version

这个项目的背景是加速器 RF 腔在高频场下会产生壁面损耗，壁面损耗导致温升，温升引起热膨胀，而几何变化会使 RF 谐振频率发生漂移。这个问题如果直接做一个多物理场模型，很容易得到结果但不知道可信不可信，所以我采用分阶段验证。

第一步是 RF eigenfrequency benchmark。我使用 COMSOL RF Module 的官方验证算例，提取频率列表，排除前三个接近零的伪模，并将最低物理模与官方参考值比较，误差在 `1e-6` 相对量级。这样保证电磁本征频率模型本身是可信的。

第二步是热学和结构的独立验证。热学部分先不用 RF 损耗，而是用受控热通量和对流冷却，检查输入热量和对流带走热量是否守恒，同时看热通量和对流系数变化时温度趋势是否合理。结构部分用铜材料热膨胀系数，施加受控温升，用 `alpha * DeltaT * L` 检查位移量级。

第三步才引入 RF-to-thermal。我先检查 COMSOL 是否有可用的 surface loss 变量，发现 PEC 本征频率模型中的自动损耗变量不适合直接使用，所以改用 RF 磁场幅值和铜表面电阻构造 `q = 0.5 * Rs * |H_t|^2`。由于 eigenmode 幅值本身是任意归一化的，我把 wall loss 归一化到指定总功率工况，再作为热通量输入热模型。

第四步是 thermal-to-structural，把稳态温度场传给 Solid Mechanics，得到径向和轴向热膨胀。最后一步是 thermal detuning：我没有把结构位移直接说成 detuning，而是用平均内壁径向位移、外壁径向位移和顶部轴向位移构造等效热几何，再重新跑 RF eigenfrequency，比较 hot 和 cold 频率。最大工况下得到约 `-31.44 kHz` 的频偏。

最后为了增强可信度，我对 cold/hot RF 对比做了 coarse、normal、fine 三档网格敏感性。三档网格的频偏差异只有约 `0.63 Hz`，说明当前 detuning 结果不是 RF 网格分辨率主导。当然项目也有边界：当前几何回灌是等效参数化近似，还不是 full deformed-boundary RF solve。

## Interview Q&A

### 共振频率怎么算？

项目里主要用 COMSOL RF Module 的 eigenfrequency study 求解本征频率。求解后提取频率列表，排除接近零的伪模，取第一个物理模作为 benchmark 频率。为了交叉检查，我也参考理想 pillbox 腔的 TM010 解析关系：

```text
f010 = x01 * c / (2*pi*R)
```

其中 `x01` 是 `J0` 的第一个零点，`R` 是理想圆柱腔半径。这个公式用于数量级理解和 benchmark 逻辑说明，实际项目频率以 COMSOL eigenfrequency 解和官方参考值对比为准。

### wall loss 怎么来的？

先尝试读取 COMSOL 的 surface loss 相关变量，但在 PEC eigenfrequency 模型中自动 wall loss 不可靠。后续采用 RF 磁场幅值和铜表面电阻推导：

```text
q = 0.5 * Rs * |H_t|^2
```

这里 `Rs` 是铜表面电阻，`H_t` 是壁面切向磁场幅值，`q` 的单位是 `W/m^2`，可以作为热通量边界。

### 为什么要归一化功率？

Eigenfrequency 模态的场幅值是任意归一化的，直接由场幅值得到的 wall loss 绝对值没有唯一物理功率含义。所以我保留场分布形状，但把总 wall loss 归一化到指定功率工况。这样可以比较不同功率下的温升、位移和 detuning 趋势。

### 热平衡怎么验证？

用轴对称边界积分检查：

```text
input power = integral(2*pi*r*q_wall)
removed heat = integral(2*pi*r*h*(T - T_amb))
```

稳态下两者应该相等。项目中的 RF 加热热平衡残差低于 `1e-8 W`，说明热源、对流边界和轴对称积分设置是闭合的。

### 温度场怎么传给结构？

在 COMSOL 中把 Heat Transfer 求得的稳态温度 `T` 作为 Solid Mechanics 的 thermal expansion 输入，参考温度设为环境温度。结构约束只固定一个必要点来去除刚体漂移，避免整边夹持导致热膨胀被过度限制。

### detuning 怎么算？

先由热结构耦合得到代表性几何变化，再构造等效热几何：

```text
a_hot      = a_cold      + average inner-wall radial displacement
b_hot      = b_cold      + average outer-wall radial displacement
height_hot = height_cold + average top-wall axial displacement
```

然后重新运行 RF eigenfrequency study，比较：

```text
delta_f = f_hot - f_cold
relative detuning = delta_f / f_cold
```

### 当前最大局限是什么？

最大局限是 Phase 6/7 的几何回灌是等效参数化近似，不是完整 deformed-boundary 或 deformed-mesh RF 求解。也就是说，当前结果适合作为可信 benchmark 和工程量级判断，但还不是高保真工业签核模型。

### COMSOL 经验如何迁移到 ANSYS / 芯片热管理？

迁移的不是某个按钮，而是验证方法。比如在 ANSYS HFSS/Mechanical 中，同样可以先做 eigenmode benchmark，再做热源映射、热平衡检查、结构热膨胀和频率回算。迁移到芯片热管理时，RF wall loss 可以换成芯片功耗密度，convection 可以换成封装边界或散热器边界，核心仍然是功率守恒、温度场可信、材料热膨胀/热阻数量级可信，以及网格敏感性检查。

