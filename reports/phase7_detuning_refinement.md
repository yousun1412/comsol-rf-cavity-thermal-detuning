# Phase 7 Thermal Detuning Refinement / Credibility Enhancement

Updated: 2026-06-26

## Scope

Phase 7 strengthens the credibility of the Phase 6 thermal-detuning result. It does not add a new physics direction. The chosen refinement path is mesh sensitivity of the RF eigenfrequency comparison.

The Phase 6 limitation remains in force: the hot/deformed RF geometry uses an equivalent parameterized-geometry approximation, not a direct deformed-mesh or full deformed-boundary RF solve.

## Refinement Choice

Chosen option:

```text
Option A: RF mesh sensitivity for cold/hot detuning comparison
```

Alternatives not performed in this phase:

- Full deformed-boundary RF feedback exploration.
- Perturbation-theory or analytical detuning estimate.

## Geometry And Cases

The mesh sensitivity uses the maximum Phase 6 thermal-deformation case:

```text
power_scale = 2.0
h = 100 W/(m^2*K)
```

Cold geometry:

```text
a_cold = 0.025 m
b_cold = 0.100 m
height_cold = 0.100 m
```

Hot equivalent geometry:

```text
a_hot      = 0.025001339523101497 m
b_hot      = 0.10000362136161217 m
height_hot = 0.10000209721066486 m
```

Geometry feedback method:

```text
a_hot      = a_cold      + average inner-wall radial displacement
b_hot      = b_cold      + average outer-wall radial displacement
height_hot = height_cold + average top-wall axial displacement
```

This is the same equivalent-geometry approximation used in Phase 6.

## Mesh Sensitivity Setup

The cold and hot RF eigenfrequency models were both solved with three COMSOL automatic mesh levels:

| Mesh level | COMSOL auto mesh size |
| --- | ---: |
| Coarse | 4 |
| Normal | 3 |
| Fine | 2 |

For each mesh level:

1. Reload the cold Phase 1 RF benchmark model.
2. Apply cold or hot geometry parameters.
3. Rebuild geometry and mesh.
4. Rerun the original RF eigenfrequency study.
5. Extract the first physical frequency from `dset2`, excluding near-zero spurious modes.
6. Compute `delta_f = f_hot - f_cold`.

## Results

Result CSV:

```text
E:\RND_Project_Portfolio\08_rf_cavity_cae_multiphysics\results\phase7\detuning_mesh_sensitivity.csv
```

| Mesh | Elements | Cold frequency (GHz) | Hot frequency (GHz) | delta_f (kHz) | Relative detuning |
| --- | ---: | ---: | ---: | ---: | ---: |
| Coarse | 692 | 1.498961963467625 | 1.498930527100663 | -31.436366961124 | -2.097209117195e-5 |
| Normal | 1426 | 1.498962200796248 | 1.498930765042486 | -31.435753762521 | -2.097167876937e-5 |
| Fine | 4770 | 1.498962282644987 | 1.498930846910465 | -31.435734521690 | -2.097166478814e-5 |

Detuning spread across the three meshes:

```text
max(delta_f) - min(delta_f) = 0.000632439434 kHz
absolute spread = 0.632439434 Hz
relative spread versus 31.436 kHz detuning ~= 2.0e-5
```

The Phase 6 baseline detuning was:

```text
-31.436667560891 kHz
```

The refined-mesh detuning values remain within about `1 Hz` of the Phase 6 baseline. This indicates that the reported detuning is not dominated by RF mesh resolution.

## Figure

Mesh sensitivity trend:

![Detuning mesh sensitivity](../results/phase7/detuning_mesh_sensitivity.png)

## Acceptance Checks

| Acceptance criterion | Result |
| --- | --- |
| Phase 6 approximation boundary retained | Passed. The report explicitly keeps the equivalent-geometry limitation. |
| At least three mesh levels used | Passed. Coarse, normal, and fine RF meshes were solved. |
| Mesh elements recorded | Passed. `692`, `1426`, and `4770` elements. |
| Cold/hot frequencies recorded | Passed for all three mesh levels. |
| `delta_f` and relative detuning recorded | Passed for all three mesh levels. |
| Stability judged | Passed. Detuning spread is about `0.63 Hz` over a `31.436 kHz` shift. |

## Phase 7 Conclusion

Phase 7 improves the credibility of the Phase 6 thermal-detuning result by showing that the equivalent-geometry RF detuning is stable under RF mesh refinement.

The key Phase 6 value:

```text
delta_f ~= -31.4367 kHz
```

is supported by the mesh sweep:

```text
coarse: -31.436366961124 kHz
normal: -31.435753762521 kHz
fine:   -31.435734521690 kHz
```

This does not upgrade Phase 6 into a full deformed-boundary high-fidelity detuning solve. It only shows that, within the equivalent-geometry approximation, the RF mesh is not the dominant uncertainty.

Recommended future refinement: attempt full deformed-boundary RF geometry mapping or add an analytical perturbation estimate as an independent order-of-magnitude cross-check.
