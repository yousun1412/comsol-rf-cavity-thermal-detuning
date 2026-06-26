# Benchmark Definition

Updated: 2026-06-26

## Project Benchmark Ladder

The project is built as a conservative validation ladder:

```text
Phase 1: RF eigenfrequency benchmark
Phase 2: standalone thermal benchmark
Phase 3: standalone structural/thermal-expansion benchmark
Phase 4: RF-to-thermal coupling with constructed wall loss
Phase 4b: field-derived RF surface-loss validation
Phase 5: thermal-to-structural coupling
Phase 6: thermal detuning by equivalent deformed-geometry RF comparison
Phase 7: thermal-detuning mesh-sensitivity refinement
```

Phase 1, Phase 2, Phase 3, Phase 4, Phase 4b, Phase 5, Phase 6, and Phase 7 are complete at this point. Phase 6 performs the first RF frequency feedback using equivalent parameterized geometry from Phase 5 deformation. Phase 7 adds RF mesh sensitivity for that detuning result. The detuning workflow remains an equivalent-geometry approximation, not a direct deformed-mesh RF solve.

## Phase 1: RF Eigenfrequency Benchmark

### Model

The Phase 1 baseline uses COMSOL's RF Module verification example:

```text
RF_Module\Verification_Examples\axisymmetric_cavity_resonator.mph
```

Project baseline model:

```text
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\pillbox_cavity_baseline.mph
```

### Solver Definition

| Item | Value |
| --- | --- |
| Software | COMSOL Multiphysics 6.4.0.293 |
| Physics | Electromagnetic Waves, Frequency Domain |
| Study type | Eigenfrequency |
| Parametric sweep | `m_azimuthal = 0, 1, 2` |
| Number of requested eigenfrequencies | 12 near 2 GHz |
| Benchmark frequency | First physical resonant mode after excluding near-zero spurious modes |

### Acceptance Criteria

| Criterion | Phase 1 result |
| --- | --- |
| RF model solves without license error | Passed |
| Solved model saved in project directory | Passed |
| Frequency list exported or recorded | Passed |
| Mesh size and DOF recorded | Passed |
| Lowest physical frequency matches official reference | Passed |

Reference comparison:

```text
Computed first physical mode: 1.498961448338762 GHz
COMSOL documentation reference: 1.49896 GHz
Relative error: 9.662290935e-7
```

The first three frequencies in the extracted `dset2` list are near zero and are classified as nonphysical/spurious modes. They are not used as benchmark resonant frequencies.

### Artifacts

```text
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase1_em_benchmark.md
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase1\frequency_table.csv
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase1\mesh_statistics.csv
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase1\pg1.png
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase1\pg2.png
```

## Phase 2: Standalone Thermal Benchmark

Phase 2 should not start from a coupled RF heating claim. Start with a controlled thermal-only problem on the fixed cavity geometry.

Recommended setup:

| Item | Recommendation |
| --- | --- |
| Geometry | Reuse/fix Phase 1 cavity geometry |
| Heat input | Apply controlled uniform heat flux on selected wall surfaces |
| Cooling | Convective cooling boundary |
| Sweep parameter | Convection coefficient `h` |
| Primary outputs | Maximum temperature, average wall temperature, total heat removed |

Expected validation trends:

- Increasing imposed heat flux increases maximum temperature.
- Increasing convection coefficient `h` decreases maximum temperature.
- At steady state, heat removed through convection is of the same order as heat input.

Only after this thermal-only benchmark is stable should RF wall loss be introduced as a heat source.

### Phase 2 Completed Baseline

| Item | Value |
| --- | --- |
| Model | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\phase2_standalone_thermal.mph` |
| Report | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase2_thermal_benchmark.md` |
| Sweep CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase2\thermal_cooling_sweep.csv` |
| Heat flux levels | `1000`, `2000 W/m^2` |
| Convection coefficients | `100`, `500`, `1000 W/(m^2*K)` |
| Mesh elements | `1426` |
| Solver time | `4.409 s` |

Acceptance status:

- Increasing heat flux increases maximum temperature.
- Increasing convection coefficient `h` decreases maximum temperature.
- Axisymmetric heat input and convective heat removal match to numerical precision.
- This remains a standalone thermal benchmark with no RF wall-loss input.

## Later Phases

## Phase 3: Standalone Structural / Thermal-Expansion Benchmark

Phase 3 verifies Solid Mechanics and thermal expansion independently from RF and heat-transfer coupling.

Completed baseline:

| Item | Value |
| --- | --- |
| Model | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\phase3_structural_thermal_expansion.mph` |
| Report | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase3_structural_benchmark.md` |
| Sweep CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase3\thermal_expansion_sweep.csv` |
| Temperature rise sweep | `1`, `5`, `10 K` |
| Copper alpha | `17e-6 1/K` |
| Mesh elements | `1426` |
| Solver time | `3.809 s` |

Acceptance status:

- Displacement increases linearly with `delta_T`.
- Radial displacement is consistent with `alpha * delta_T * 0.1 m`.
- Maximum displacement is the same order as `alpha * delta_T * sqrt(0.1^2 + 0.1^2)`.
- This remains a standalone structural benchmark with no RF wall-loss input and no coupled multiphysics claim.

## Phase 4: RF-to-Thermal Coupling

Phase 4 maps a documented RF wall-loss heat source into the thermal model. It does not include structural coupling or frequency detuning.

Completed baseline:

| Item | Value |
| --- | --- |
| Model | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\phase4_rf_thermal_coupling.mph` |
| Report | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase4_rf_thermal_coupling.md` |
| Sweep CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase4\rf_thermal_sweep.csv` |
| Power scales | `0.5`, `1.0`, `2.0` |
| Total wall loss | `10`, `20`, `40 W` |
| Convection coefficients | `100`, `500`, `1000 W/(m^2*K)` |
| Mesh elements | `1426` |
| Solver time | `4.248 s` |

Acceptance status:

- RF wall-loss total power is recorded explicitly.
- Heat input and convective heat removal match to below `1e-10 W`.
- Increasing power scale increases temperature rise.
- Increasing convection coefficient `h` decreases temperature rise.
- This remains RF-to-thermal coupling only; no structural deformation or detuning is solved.

## Phase 4b: Field-Derived RF Surface-Loss Validation

Phase 4b replaces the constructed Phase 4 wall-loss shape with a loss distribution derived from the Phase 1 RF magnetic field. It remains RF-to-thermal only and does not include structural deformation or frequency detuning.

Completed baseline:

| Item | Value |
| --- | --- |
| Model | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\phase4b_field_derived_rf_loss.mph` |
| Report | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase4b_field_derived_rf_loss.md` |
| Surface-loss CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase4b\field_surface_loss.csv` |
| Thermal sweep CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase4b\rf_thermal_field_loss_sweep.csv` |
| Source RF frequency | `1.498961448338762 GHz` |
| Direct COMSOL loss-variable audit | `emw.Qsh = 0.0`; not usable as conductor wall loss for this PEC eigenfrequency model |
| Accepted loss derivation | `q = 0.5*Rs*|H_t|^2`, with copper `Rs = 0.0101009239958317 ohm` |
| Raw integrated field loss | `0.000159155881647966 W` |
| Normalized reference power | `20 W` |
| Power scales | `0.5`, `1.0`, `2.0` |
| Total wall loss | `10`, `20`, `40 W` |
| Convection coefficients | `100`, `500`, `1000 W/(m^2*K)` |
| Mesh elements | `1426` |
| Solver time | `4.486 s` |

Acceptance status:

- Automatic COMSOL loss variables were checked; no reliable nonzero conductor surface loss was available from the PEC eigenfrequency model.
- Field-derived copper surface loss has an explicit `W/m^2` conversion.
- Axisymmetric boundary integration closes the normalized wall loss to `10`, `20`, and `40 W`.
- Heat input and convective heat removal match with maximum residual `7.6e-11 W`.
- Increasing RF power scale increases temperature rise.
- Increasing convection coefficient `h` decreases temperature rise.
- Phase 4b remains RF-to-thermal only; no structural deformation or thermal detuning is solved.

## Phase 5: Thermal-to-Structural Coupling

Phase 5 uses the audited Phase 4b field-derived RF heat source in a stationary thermal solve, then passes the resulting temperature field to Solid Mechanics through copper thermal expansion. It does not include RF frequency recalculation or thermal detuning.

Completed baseline:

| Item | Value |
| --- | --- |
| Model | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\phase5_thermal_structural_coupling.mph` |
| Report | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase5_thermal_structural_coupling.md` |
| Sweep CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase5\thermal_structural_sweep.csv` |
| Heat source | Phase 4b field-derived RF wall loss |
| Power scales | `0.5`, `1.0`, `2.0` |
| Total wall loss | `10`, `20`, `40 W` |
| Convection coefficients | `100`, `500`, `1000 W/(m^2*K)` |
| Copper alpha | `17e-6 1/K` |
| Mesh elements | `1426` |
| Degrees of freedom | `8847` |
| Solver time | `7.774 s` Java timer; `24 s` COMSOL log total |
| Maximum displacement case | `7.489982458994 um` at `power_scale=2`, `h=100 W/(m^2*K)` |

Acceptance status:

- Increasing RF power scale increases both temperature rise and displacement.
- Increasing convection coefficient `h` decreases both temperature rise and displacement.
- Displacement is consistent with `alpha * DeltaT * L` order of magnitude.
- Phase 5 remains thermal-to-structural coupling only; RF frequency feedback and thermal detuning are not included.

## Phase 6: Thermal Detuning / Deformed-Geometry RF Eigenfrequency Comparison

Phase 6 feeds Phase 5 thermal-expansion results back into the RF eigenfrequency model. The geometry feedback is an equivalent parameterized approximation:

```text
a_hot      = a_cold      + average inner-wall radial displacement
b_hot      = b_cold      + average outer-wall radial displacement
height_hot = height_cold + average top-wall axial displacement
```

Completed baseline:

| Item | Value |
| --- | --- |
| Model | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\phase6_thermal_detuning.mph` |
| Report | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase6_thermal_detuning.md` |
| Result CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase6\thermal_detuning_results.csv` |
| Cold frequency | `1.498961448338762 GHz` |
| Maximum-case hot frequency | `1.4989300116712012 GHz` |
| Maximum-case detuning | `-31.436667560891 kHz` |
| Maximum-case relative detuning | `-2.097229891785e-5` |
| Medium-case hot frequency | `1.4989575395888475 GHz` |
| Medium-case detuning | `-3.908749914627 kHz` |
| Mesh elements | `428` |

Acceptance status:

- Cold and hot RF eigenfrequencies are explicitly recorded.
- `delta_f` and relative detuning are computed.
- The geometry feedback method is documented as an equivalent parameterized approximation.
- RF eigenfrequency was rerun after geometry parameter updates; this is not only a structural displacement estimate.

## Phase 7: Thermal Detuning Refinement / Credibility Enhancement

Phase 7 strengthens the Phase 6 detuning result by running RF mesh sensitivity on the maximum thermal-deformation case. It does not introduce new physics and does not change the Phase 6 geometry-feedback approximation.

Completed baseline:

| Item | Value |
| --- | --- |
| Report | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase7_detuning_refinement.md` |
| Result CSV | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase7\detuning_mesh_sensitivity.csv` |
| Figure | `E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase7\detuning_mesh_sensitivity.png` |
| Mesh levels | coarse, normal, fine |
| Mesh elements | `692`, `1426`, `4770` |
| Coarse detuning | `-31.436366961124 kHz` |
| Normal detuning | `-31.435753762521 kHz` |
| Fine detuning | `-31.435734521690 kHz` |
| Detuning spread | `0.632439434 Hz` |

Acceptance status:

- Phase 6 equivalent-geometry approximation is retained and documented.
- Three RF mesh levels were solved for both cold and hot geometry.
- Cold frequency, hot frequency, `delta_f`, relative detuning, mesh elements, and solve time are recorded.
- Detuning is stable to about `0.63 Hz` over a `31.436 kHz` shift, so RF mesh resolution is not the dominant uncertainty in the Phase 6 approximation.

## Later Phases

Remaining future work:

- Improve Phase 6/7 geometry fidelity by exporting or mapping the full deformed RF boundary instead of using only equivalent `a`, `b`, and `height` changes.
- Add an analytical perturbation estimate or independent solver comparison if this benchmark is used for quantitative design decisions.
