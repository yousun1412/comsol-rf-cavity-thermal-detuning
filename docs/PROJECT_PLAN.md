# Project Plan: RF Cavity Electro-Thermal-Mechanical CAE Workflow

更新时间：2026-06-26

## 1. 项目目标

建立一个可验证的加速器 RF 腔多物理场仿真项目，主线为：

```text
电磁 eigenmode
-> 腔壁 RF 损耗
-> 热传导与冷却边界
-> 热膨胀/结构变形
-> 谐振频率漂移 thermal detuning
```

项目强调工程仿真流程、验证口径和参数敏感性，而不是从零手写商业 CAE 求解器。

## 2. 软件路线

### 主线：COMSOL

所需模块：

- COMSOL Multiphysics
- RF Module
- Heat Transfer Module
- Structural Mechanics Module

COMSOL 用途：

- RF eigenfrequency / field distribution
- wall loss / RF heating
- Heat Transfer in Solids
- Solid Mechanics / Thermal Expansion
- deformed geometry eigenfrequency 或频率漂移估计

### 二阶段：ANSYS

所需模块或环境：

- ANSYS Electronics Desktop / HFSS
- ANSYS Mechanical
- Workbench coupling，若许可允许

ANSYS 用途：

- 复现 pillbox cavity eigenfrequency。
- 复现热通量驱动下的温度场和热变形。
- 与 COMSOL 关键指标做交叉验证。

### 备选：CST

用途：

- RF cavity eigenmode / field / wall current / loss 参考。

边界：

- 若热/结构模块不可用，CST 不作为完整多物理场主线。

## 3. 阶段安排

### Phase 0: 软件部署与 benchmark 设计

输入：

- COMSOL / ANSYS / CST 下载和安装状态。
- 本目录已有开源参考和文献资料。

任务：

1. 记录已安装软件版本、许可类型、模块可用性。
2. 跑通一个官方最小案例，确认软件能求解。
3. 设计 pillbox cavity benchmark 参数。
4. 写清楚每个物理场验证标准。

交付：

- `docs/software_installation_log.md`
- `docs/benchmark_definition.md`
- `docs/phase0_acceptance_checklist.md`

验收：

- 明确 COMSOL/ANSYS/CST 哪个可用。
- 明确第一版用哪个软件推进。
- 明确 TM010 频率解析验证公式。

### Phase 1: 单独电磁仿真

目标：

- 建立 pillbox RF cavity eigenmode 模型。
- 验证 TM010 模式频率。

任务：

1. 建立 pillbox cavity 几何。
2. 设置 PEC 或铜材料边界。
3. 求解 eigenfrequency。
4. 提取 E/H 场分布。
5. 与解析频率对比。

验证：

```text
relative_error = abs(f_sim - f_analytic) / f_analytic
```

建议阈值：

- 初版 < 5%
- 网格收敛后 < 1%

输出：

- `results/em_eigenmode_frequency.csv`
- `results/figures/em_field_pattern.png`
- `reports/phase1_em_benchmark.md`

### Phase 2: 单独热仿真

目标：

- 不依赖复杂 EM 场，先验证热传导和冷却边界设置。

任务：

1. 使用同一腔体壁或简化铜环/铜块。
2. 设置均匀热通量或局部热源。
3. 设置外壁 convection cooling。
4. 扫描 heat-transfer coefficient `h`。
5. 做能量平衡和趋势验证。

验证：

- 输入热功率增加，温升增加。
- `h` 增加，温升降低。
- 简化几何下温升量级与热阻估算一致。

输出：

- `results/thermal_cooling_sweep.csv`
- `results/figures/temperature_field.png`
- `reports/phase2_thermal_benchmark.md`

### Phase 3: EM-to-Thermal 耦合

目标：

- 将 RF wall loss 映射为热通量，求解温度场。

核心关系：

```text
q_RF = 0.5 * Rs * |H_t|^2
```

任务：

1. 从 EM 解中提取 wall loss / surface loss。
2. 将 wall loss 作为 Heat Transfer 边界热源。
3. 计算 steady-state temperature field。
4. 扫描 RF power 与 cooling coefficient。

输出指标：

- total wall loss
- max temperature
- temperature rise
- hotspot location

输出：

- `results/em_thermal_coupling_sweep.csv`
- `results/figures/wall_loss_distribution.png`
- `results/figures/em_heating_temperature_field.png`
- `reports/phase3_em_thermal_coupling.md`

### Phase 4: Thermal-to-Structural 耦合

目标：

- 根据温度场计算热变形。

任务：

1. 设置 Solid Mechanics。
2. 设置 copper thermal expansion coefficient。
3. 传入 temperature field。
4. 设置合理固定约束，避免刚体漂移。
5. 提取最大位移和关键半径/长度变化。

验证：

```text
displacement_scale ~ alpha * delta_T * L
```

输出：

- `results/thermal_deformation_metrics.csv`
- `results/figures/deformation_field.png`
- `reports/phase4_thermal_structural.md`

### Phase 5: Frequency Detuning

目标：

- 评估热变形导致的谐振频率漂移。

路线 A：

1. 使用 deformed geometry 重新求 eigenfrequency。
2. 比较 cold/hot frequency。

路线 B：

1. 使用解析近似或 perturbation 方法估算。
2. 与路线 A 对比。

输出：

- `f_cold`
- `f_hot`
- `delta_f`
- `delta_f_vs_power`
- `delta_f_vs_h`

输出：

- `results/frequency_detuning_sweep.csv`
- `results/figures/detuning_vs_power_and_cooling.png`
- `reports/phase5_detuning_analysis.md`

## 4. 参数建议

第一版只扫少量参数：

| 参数 | 建议值 | 含义 |
| --- | --- | --- |
| RF power scale | 0.5, 1.0, 2.0 | 等效输入功率或损耗缩放 |
| cooling coefficient h | 100, 500, 1000 W/m2/K | 外壁冷却强度 |
| cavity radius | baseline +/- 1% | 几何敏感性 |
| wall thickness | 2, 5, 10 mm | 热阻与变形敏感性 |

## 5. 项目最终交付

最终交付目标：

1. CAE model files。
2. 参数扫描 CSV。
3. EM field / wall loss / temperature / deformation / detuning 图。
4. 分阶段验证报告。
5. 最终求职包装报告。

最终报告标题：

```text
基于 COMSOL/ANSYS 的加速器 RF 腔电磁-热-结构耦合与热失谐分析
```

如果只完成 COMSOL：

```text
基于 COMSOL 的加速器 RF 腔电磁-热-结构耦合与热失谐分析
```

## 6. 风险控制

高风险点：

- 商业软件模块缺失。
- RF eigenmode 设置错误。
- wall loss 和热通量单位映射错误。
- 结构约束设置不合理。
- 频率漂移过度解读。

控制方式：

- 每阶段单独验证。
- 先 benchmark，再耦合。
- 每个物理场都有 sanity check。
- 不把 benchmark 腔体说成真实工程设计。

