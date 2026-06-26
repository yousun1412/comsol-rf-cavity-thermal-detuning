# Phase 4b Field-Derived RF Surface Loss Validation

Updated: 2026-06-26

## Scope

Phase 4b validates a field-derived RF surface-loss heat source for the thermal model. It starts from the Phase 1 RF eigenfrequency solution, derives a copper surface-loss distribution from the RF magnetic-field magnitude on the inner wall, scales it to a documented total power, and reruns the Phase 4 thermal sweep.

This phase is RF-to-thermal only. It does not include structural deformation, thermal detuning, or full RF-thermal-structural coupling.

## Source Model And Surface-Loss Variable Audit

Phase 1 RF model:

```text
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\pillbox_cavity_baseline.mph
```

Phase 4b thermal model:

```text
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\models\comsol\phase4b_field_derived_rf_loss.mph
```

COMSOL variable probe on the Phase 1 RF solution found:

| Expression | Probe result | Interpretation |
| --- | ---: | --- |
| `emw.Qsh` | `0.0` | Variable exists, but the PEC eigenfrequency benchmark does not provide usable conductor surface loss. |
| `emw.Qrh` | `8.279158842086304e-23` | Negligible volumetric loss-like quantity for this benchmark setup. |
| `emw.Qh` | `8.279158841989706e-23` | Negligible. |
| `emw.Qe` | `8.279158841989706e-23` | Negligible. |
| `emw.Ploss` | `-5.392355517163621e-27` | Not a usable positive wall-loss total. |
| `emw.normH`, `emw.Hphi`, `emw.Hz` | Available | Used to derive surface loss from RF magnetic-field magnitude. |

Conclusion: a direct automatic COMSOL conductor-loss variable was not reliable for this PEC eigenfrequency model. Phase 4b therefore uses field-derived surface loss from the Phase 1 magnetic field, with the derivation and scaling recorded explicitly.

## Field-Derived Surface Loss

Physical mode used:

```text
Frequency: 1.498961448338762 GHz
```

Sampled RF field:

```text
sqrt(abs(emw.Hphi)^2 + abs(emw.Hz)^2)
```

Sampling boundary:

```text
Inner wall radius r = 0.025 m
z = 0..0.1 m, 41 samples
```

Copper surface resistance:

```text
Rs = sqrt(pi*f*mu0/sigma)
sigma = 5.8e7 S/m
Rs = 0.0101009239958317 ohm
```

Surface-loss conversion:

```text
q_raw = 0.5 * Rs * |H_t|^2
```

Units:

```text
Rs: ohm
H_t: A/m
q_raw: ohm * A^2/m^2 = W/m^2
```

Axisymmetric integration on the inner wall:

```text
P_raw = integral_0^height 2*pi*a*q_raw dz
P_raw = 0.000159155881647966 W
```

The RF eigenmode amplitude is arbitrary, so the raw loss is normalized to the Phase 4 reference total power:

```text
P_target = 20 W
scale_to_20W = P_target / P_raw = 125662.965093791
q_scaled = q_raw * scale_to_20W
```

Exported field-loss table:

```text
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase4b\field_surface_loss.csv
```

The sampled distribution is symmetric and peaks near the two axial ends of the inner wall. For the COMSOL thermal rerun, the sampled trend was represented by the analytic field-derived fit:

```text
field_wall_shape = 2*cos(pi*z/height)^2
wall_loss_flux = P0_rf*power_scale*field_wall_shape/(2*pi*a*height)
P0_rf = 20 W
```

This keeps the total wall loss exactly normalized:

```text
integral_inner_wall(2*pi*r*wall_loss_flux) = P0_rf * power_scale
```

## Solver Metrics

| Metric | Value |
| --- | ---: |
| COMSOL version | COMSOL Multiphysics 6.4.0.293 |
| Study type | Stationary heat transfer |
| Mesh elements | `1426` |
| Mesh vertices | `762` |
| Solve elapsed time | `4.486 s` |
| License error observed | No |

## Thermal Sweep Results

Sweep CSV:

```text
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase4b\rf_thermal_field_loss_sweep.csv
```

| power_scale | h (W/m^2/K) | total wall loss (W) | Tmax (K) | Max rise (K) | Convective heat removed (W) | Residual (W) | Delta vs Phase 4 constructed rise (K) |
| ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 0.5 | 100 | 10 | 294.029911908803 | 0.879911908803 | 10.000000000019 | -1.9e-11 | 0.009051311033 |
| 0.5 | 500 | 10 | 293.371857259425 | 0.221857259425 | 10.000000000005 | -5.0e-12 | 0.008866954181 |
| 0.5 | 1000 | 10 | 293.288675514951 | 0.138675514951 | 10.000000000003 | -3.0e-12 | 0.008645330598 |
| 1.0 | 100 | 20 | 294.909823817606 | 1.759823817606 | 20.000000000038 | -3.8e-11 | 0.018102622065 |
| 1.0 | 500 | 20 | 293.593714518851 | 0.443714518851 | 20.000000000009 | -9.0e-12 | 0.017733908362 |
| 1.0 | 1000 | 20 | 293.427351029901 | 0.277351029901 | 20.000000000006 | -6.0e-12 | 0.017290661196 |
| 2.0 | 100 | 40 | 296.669647635212 | 3.519647635213 | 40.000000000076 | -7.6e-11 | 0.036205244131 |
| 2.0 | 500 | 40 | 294.037429037702 | 0.887429037702 | 40.000000000018 | -1.8e-11 | 0.035467816724 |
| 2.0 | 1000 | 40 | 293.704702059802 | 0.554702059802 | 40.000000000010 | -1.0e-11 | 0.034581322391 |

Maximum absolute heat-balance residual:

```text
7.6e-11 W
```

## Figures

Field-derived wall-loss distribution:

![Field wall loss distribution](../results/phase4b/field_wall_loss_distribution.png)

Temperature field under field-derived RF heating:

![Field loss temperature field](../results/phase4b/field_loss_temperature_field.png)

## Acceptance Checks

| Acceptance criterion | Result |
| --- | --- |
| Surface-loss variable source and units documented | Passed. Automatic `emw.Qsh` was audited and found unusable for this PEC model; the accepted source is `|H_t|` from Phase 1 RF fields with `q = 0.5*Rs*|H_t|^2`, in `W/m^2`. |
| Total wall loss closes by boundary integration | Passed. Raw field loss integrates to `0.000159155881647966 W`; normalized model integrates to `10`, `20`, and `40 W`. |
| Heat input and convection removal residual below `1e-8 W` | Passed. Maximum absolute residual is `7.6e-11 W`. |
| Increasing `power_scale` increases temperature rise | Passed at every `h`. |
| Increasing `h` lowers temperature rise | Passed at every power scale. |
| No structural coupling or detuning claimed | Passed. Phase 4b remains RF-to-thermal only. |

## Phase 4b Conclusion

Phase 4b is complete as a field-derived RF surface-loss validation. The directly probed COMSOL automatic loss variables did not provide reliable conductor wall loss for the PEC eigenfrequency example, so the validated path uses the Phase 1 RF magnetic field, copper surface resistance, explicit `W/m^2` conversion, and total-power normalization to `20 W`.

Compared with the constructed Phase 4 wall-loss distribution, the field-derived distribution gives slightly higher maximum temperature rise by about `0.009 K` at `10 W`, `0.018 K` at `20 W`, and `0.036 K` at `40 W` for `h = 100 W/(m^2*K)`. The heat balance remains closed to numerical precision.

Recommended next step: use this field-derived RF heat source as the audited input for a later thermal-to-structural coupling phase, still without claiming thermal detuning until the deformed geometry is explicitly fed back into an RF eigenfrequency solve.
