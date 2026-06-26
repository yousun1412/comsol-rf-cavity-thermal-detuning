# Software Installation Log

Updated: 2026-06-26

## COMSOL

Status: installed and verified for Phase 1 RF eigenfrequency benchmark.

| Item | Value |
| --- | --- |
| Version | COMSOL Multiphysics 6.4.0.293 |
| License number | 6464550 |
| Install path | `E:\COSMOL\comsol1` |
| GUI launcher | `E:\COSMOL\comsol1\bin\win64\comsol.exe` |
| Batch launcher | `E:\COSMOL\comsol1\bin\win64\comsolbatch.exe` |
| RF Module application library | Present: `E:\COSMOL\comsol1\applications\RF_Module` |
| Heat Transfer Module application library | Present: `E:\COSMOL\comsol1\applications\Heat_Transfer_Module` |
| Structural Mechanics Module application library | Present: `E:\COSMOL\comsol1\applications\Structural_Mechanics_Module` |
| RF Module license checkout | Verified by solving RF Module eigenfrequency example |
| Heat Transfer Module license checkout | Pending Phase 2 standalone thermal run |
| Structural Mechanics Module license checkout | Pending later structural run |

Verified commands:

```powershell
& 'E:\COSMOL\comsol1\bin\win64\comsolbatch.exe' -help
& 'E:\COSMOL\comsol1\bin\win64\comsolbatch.exe' -version
```

Phase 1 verified run:

```text
Model: E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\pillbox_cavity_baseline.mph
Batch log: E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\pillbox_cavity_baseline_batch.log
Report: E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\reports\phase1_em_benchmark.md
```

Observed Phase 1 result:

| Item | Value |
| --- | --- |
| Study | RF eigenfrequency |
| Parametric sweep | `m_azimuthal = 0, 1, 2` |
| Lowest physical resonant frequency | `1.498961448338762 GHz` |
| Mesh elements | `428` |
| Degrees of freedom | `3101` |
| COMSOL log total time | `16 s` |
| License errors | None observed |

Relevant installed application-library models:

```text
E:\COSMOL\comsol1\applications\RF_Module\Verification_Examples\axisymmetric_cavity_resonator.mph
E:\COSMOL\comsol1\applications\RF_Module\Verification_Examples\cavity_resonators.mph
E:\COSMOL\comsol1\applications\RF_Module\Filters\cavity_filter_thermal_expansion.mph
E:\COSMOL\comsol1\applications\RF_Module\Microwave_Heating\rf_heating.mph
```

Note: the installed `axisymmetric_cavity_resonator.mph` was a COMSOL Application Libraries preview file. The complete COMSOL 6.4 model was downloaded from COMSOL Application ID `14517` before solving.

## ANSYS

Status: pending / not used for Phase 1.

To record after installation:

| Item | Value |
| --- | --- |
| Version | TBD |
| License type | TBD |
| Install path | TBD |
| HFSS available | TBD |
| Mechanical available | TBD |
| Workbench available | TBD |
| Official example run | TBD |

## CST

Status: optional / pending.

To record after installation:

| Item | Value |
| --- | --- |
| Version | TBD |
| License type | TBD |
| Install path | TBD |
| Eigenmode solver available | TBD |
| Thermal coupling available | TBD |
