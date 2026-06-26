# Final Project Summary

## Project Background

RF cavities can drift away from their target resonant frequency when RF wall loss heats the cavity wall. The heat changes the temperature field, the cavity expands, and the deformed geometry shifts the RF eigenfrequency. This project packages that chain into a staged COMSOL benchmark:

```text
RF field -> wall loss -> heat flux -> temperature -> thermal expansion -> hot RF frequency
```

The work is intentionally split into small validation steps. Each phase answers one question before moving to the next one, so the final detuning result is traceable rather than a black-box multiphysics run.

## Electromagnetic Simulation

The RF baseline starts from COMSOL's RF cavity eigenfrequency verification example. The first task was to reproduce the cold eigenfrequency benchmark and identify the first physical mode after excluding near-zero spurious modes.

Main checks:

- Eigenfrequency study solved successfully with the RF Module.
- The lowest physical frequency matched the COMSOL reference at about `1e-6` relative error.
- Near-zero modes were explicitly treated as spurious and not used as benchmark frequencies.
- Field plots and frequency tables were exported for review.

Primary artifacts:

- `reports/phase1_em_benchmark.md`
- `results/phase1/frequency_table.csv`
- `results/phase1/pg1.png`

## Thermal Simulation

The thermal model was first verified as a standalone problem before RF heating was introduced. A controlled wall heat flux and convection boundary were used to check whether the model obeys the expected thermal trends.

Main checks:

- Increasing heat input increases the maximum temperature.
- Increasing convection coefficient lowers the maximum temperature.
- Integrated heat input and convective heat removal close to numerical precision.
- Temperature-field plots and sweep tables were exported.

Primary artifacts:

- `reports/phase2_thermal_benchmark.md`
- `results/phase2/thermal_cooling_sweep.csv`
- `results/phase2/temperature_field.png`

## RF-To-Thermal Coupling

After the standalone thermal check, RF wall heating was introduced. The final heat source used in the benchmark is derived from RF magnetic-field magnitude and copper surface resistance:

```text
q = 0.5 * Rs * |H_t|^2
```

Because eigenmode amplitude is arbitrary, the raw field-derived wall loss was normalized to prescribed total-power cases. This gives a controlled heat input while preserving the RF-derived wall-loss distribution shape.

Main checks:

- COMSOL automatic surface-loss variables were audited and found unsuitable for the PEC eigenfrequency model.
- RF magnetic-field magnitude was used to construct a field-derived wall-loss distribution.
- Surface loss was converted to heat flux with explicit units.
- Total input power and convective heat removal matched with residual below `1e-8 W`.

Primary artifacts:

- `reports/phase4b_field_derived_rf_loss.md`
- `results/phase4b/field_surface_loss.csv`
- `results/phase4b/rf_thermal_field_loss_sweep.csv`
- `results/phase4b/field_wall_loss_distribution.png`

## Thermal-Structural Coupling

The Phase 4b temperature field was passed to Solid Mechanics through copper thermal expansion. A minimal structural constraint was used to remove rigid-body drift without clamping the cavity wall.

Main checks:

- Higher RF power produced higher temperature rise and larger displacement.
- Stronger convection reduced both temperature rise and displacement.
- Maximum displacement was consistent with the `alpha * DeltaT * L` order-of-magnitude estimate.
- Representative radius and length changes were extracted for RF geometry feedback.

Primary artifacts:

- `reports/phase5_thermal_structural_coupling.md`
- `results/phase5/thermal_structural_sweep.csv`
- `results/phase5/displacement_field_from_rf_heating.png`

## Thermal Detuning

Thermal detuning was evaluated by feeding an equivalent hot geometry back into the RF eigenfrequency model:

```text
a_hot      = a_cold      + average inner-wall radial displacement
b_hot      = b_cold      + average outer-wall radial displacement
height_hot = height_cold + average top-wall axial displacement
```

This is an equivalent parameterized-geometry approximation. It is not a full deformed-boundary RF solve. After updating the RF geometry parameters, the eigenfrequency study was rerun and compared against the cold baseline.

Main checks:

- Cold and hot RF frequencies were explicitly recorded.
- Frequency shift and relative detuning were calculated.
- The RF solve was rerun after geometry feedback; detuning was not inferred from displacement alone.
- Coarse / normal / fine RF mesh sensitivity showed the detuning result was stable to within about `0.63 Hz`.

Primary artifacts:

- `reports/phase6_thermal_detuning.md`
- `reports/phase7_detuning_refinement.md`
- `results/phase6/thermal_detuning_results.csv`
- `results/phase7/detuning_mesh_sensitivity.csv`

## Core Results

| Item | Result |
| --- | --- |
| Cold RF benchmark | First physical eigenfrequency matches COMSOL reference at about `1e-6` relative error |
| RF wall loss | Field-derived heat flux from `0.5 * Rs * |H_t|^2`, normalized to controlled total-power cases |
| Thermal balance | Input wall loss and convective heat removal agree to below `1e-8 W` in the RF-heated sweep |
| Structural scale | Displacement follows the expected `alpha * DeltaT * L` order of magnitude |
| Maximum-case detuning | About `-31.44 kHz`, relative detuning about `-2.1e-5` |
| Mesh sensitivity | Coarse / normal / fine detuning spread about `0.63 Hz` |

## Current Limitations

- The hot RF geometry uses equivalent `a`, `b`, and `height` changes rather than a full deformed boundary.
- The wall-loss magnitude is normalized because RF eigenmode amplitude is arbitrary.
- Cross-software validation with HFSS, CST, ACE3P, Elmer, or another solver has not been performed.
- The model is a portfolio benchmark and validation workflow, not an industrial RF cavity design sign-off.

