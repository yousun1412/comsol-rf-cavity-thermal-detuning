# RF Cavity Thermal Detuning Benchmark in COMSOL

This repository contains a staged COMSOL study of a pillbox-like RF cavity, starting from an eigenfrequency benchmark and ending with an approximate thermal-detuning check.

The project was built as a validation exercise rather than a single "all-in-one" multiphysics model. Each step has a small acceptance test: frequency agreement, heat balance, thermal-expansion scale, power/cooling trends, detuning comparison, and mesh sensitivity.

## Why This Project

In an RF cavity, electromagnetic fields can create wall loss. Wall loss heats the cavity, the cavity expands, and the RF eigenfrequency shifts. That chain sounds straightforward, but it is easy to make a simulation look coupled without checking whether each link is physically sensible.

So I split the workflow into separate, testable pieces:

```text
RF eigenfrequency
standalone thermal
standalone structural expansion
RF wall loss -> thermal
thermal field -> structural expansion
deformed/equivalent geometry -> RF eigenfrequency
RF mesh sensitivity
```

The main point is traceability: every later result has a documented source and a numerical sanity check behind it.

## What Is In The Repository

```text
docs/       Project definition and benchmark notes
reports/    Phase-by-phase writeups
results/    Exported CSV tables and figures
tools/      COMSOL Java API scripts used during the study
handoff/    Notes for continuing the work
```

The most useful files to read first are:

- `reports/phase1_em_benchmark.md`
- `reports/phase4b_field_derived_rf_loss.md`
- `reports/phase5_thermal_structural_coupling.md`
- `reports/phase6_thermal_detuning.md`
- `reports/phase7_detuning_refinement.md`

## Validation Flow

| Phase | What was checked | Main artifact |
| --- | --- | --- |
| 1 | RF eigenfrequency benchmark against COMSOL reference | `reports/phase1_em_benchmark.md` |
| 2 | Thermal model with prescribed heat flux and convection | `reports/phase2_thermal_benchmark.md` |
| 3 | Copper thermal expansion under controlled temperature rise | `reports/phase3_structural_benchmark.md` |
| 4 | RF-to-thermal mapping with a normalized wall-loss shape | `reports/phase4_rf_thermal_coupling.md` |
| 4b | Field-derived wall loss from RF magnetic field magnitude | `reports/phase4b_field_derived_rf_loss.md` |
| 5 | Temperature-field transfer into Solid Mechanics | `reports/phase5_thermal_structural_coupling.md` |
| 6 | RF frequency shift after equivalent geometry feedback | `reports/phase6_thermal_detuning.md` |
| 7 | RF mesh sensitivity of the detuning result | `reports/phase7_detuning_refinement.md` |

## Key Checks

The cold RF eigenfrequency benchmark matches the COMSOL reference at roughly the `1e-6` relative-error level.

The thermal stages check energy balance by comparing integrated heat input and convective heat removal. The structural stages compare displacement scale against `alpha * DeltaT * L`.

For RF heating, the field-derived wall-loss expression is:

```text
q = 0.5 * Rs * |H_t|^2
```

The raw eigenmode field amplitude is arbitrary, so the wall loss is normalized to prescribed total-power cases before being used as a heat source.

## Thermal Detuning Result

The current detuning workflow uses an equivalent geometry feedback:

```text
a_hot      = a_cold      + average inner-wall radial displacement
b_hot      = b_cold      + average outer-wall radial displacement
height_hot = height_cold + average top-wall axial displacement
```

For the largest thermal case in this study, the RF frequency shift is about:

```text
delta_f ~= -31.44 kHz
relative detuning ~= -2.1e-5
```

This is not presented as a high-fidelity deformed-boundary result. It is an engineering approximation that closes the loop from thermal expansion back to RF frequency.

## Mesh Sensitivity

To check whether the detuning result was just a mesh artifact, the cold/hot RF comparison was repeated with three RF mesh levels:

```text
coarse: -31.4364 kHz
normal: -31.4358 kHz
fine:   -31.4357 kHz
```

The spread is about `0.63 Hz` over a `31 kHz`-level frequency shift. Within the equivalent-geometry approximation, RF mesh resolution is not the dominant uncertainty.

## Figures

Thermal detuning comparison:

![Cold vs hot RF frequency](results/phase6/cold_vs_hot_frequency.png)

Mesh sensitivity:

![Detuning mesh sensitivity](results/phase7/detuning_mesh_sensitivity.png)

## What Is Not Included

COMSOL binary model files are not committed:

```text
*.mph
*.mph.lock
*.mph.recovery
*.mph.status
```

The repository also excludes COMSOL application-library files, COMSOL PDFs, and large third-party reference repositories. This keeps the repo small and avoids redistributing files whose license status is not mine to decide.

The included Java scripts and exported CSV/PNG files are enough to show the modeling logic, numerical checks, and result trail.

## Limitations

- The thermal-detuning step uses equivalent geometry parameters, not a full deformed mesh.
- The field-derived wall loss is normalized because RF eigenmode amplitudes are arbitrary.
- Cross-software validation against HFSS, CST, ACE3P, Elmer, or another solver has not been done.
- The project is a benchmark workflow, not an industrial RF cavity design sign-off.

## Possible Next Steps

- Map the full deformed RF boundary instead of reducing deformation to `a`, `b`, and `height`.
- Add a perturbation-theory estimate for the frequency shift.
- Repeat the same staged workflow on a more realistic cavity shape.

