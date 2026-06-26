# COMSOL RF Cavity Thermal Detuning Benchmark

This repository documents a staged COMSOL benchmark for RF cavity multiphysics and thermal detuning analysis.

The project follows a conservative verification ladder:

```text
RF eigenfrequency
-> standalone thermal
-> standalone structural expansion
-> RF-to-thermal heating
-> thermal-to-structural expansion
-> thermal detuning by RF eigenfrequency comparison
-> mesh-sensitivity refinement
```

## Motivation

Accelerator RF cavities can experience wall heating, thermal expansion, and resonant-frequency shift under high-frequency electromagnetic fields. Instead of treating the full coupled workflow as one opaque simulation, this project decomposes the problem into independently checkable phases.

The goal is not to build a new FEM solver. The goal is to create a reproducible CAE validation workflow with clear assumptions, numerical checks, and engineering boundaries.

## Highlights

- Reproduced an official COMSOL RF cavity eigenfrequency benchmark with relative error on the order of `1e-6`.
- Built standalone thermal and structural baselines before introducing coupling.
- Derived an RF wall-loss heat source from magnetic-field magnitude and copper surface resistance.
- Mapped RF heating into temperature, temperature into structural expansion, and equivalent geometry back into RF eigenfrequency.
- Estimated thermal detuning using an equivalent parameterized geometry approximation.
- Verified the detuning result with coarse / normal / fine RF mesh sensitivity; detuning spread stayed below `1 Hz` over an approximately `31 kHz` shift.

## Project Phases

| Phase | Scope | Key output |
| --- | --- | --- |
| Phase 1 | RF eigenfrequency benchmark | `reports/phase1_em_benchmark.md` |
| Phase 2 | Standalone thermal benchmark | `reports/phase2_thermal_benchmark.md` |
| Phase 3 | Standalone structural expansion | `reports/phase3_structural_benchmark.md` |
| Phase 4 | RF-to-thermal with constructed wall loss | `reports/phase4_rf_thermal_coupling.md` |
| Phase 4b | Field-derived RF surface loss | `reports/phase4b_field_derived_rf_loss.md` |
| Phase 5 | Thermal-to-structural coupling | `reports/phase5_thermal_structural_coupling.md` |
| Phase 6 | Thermal detuning by equivalent geometry | `reports/phase6_thermal_detuning.md` |
| Phase 7 | Detuning mesh-sensitivity refinement | `reports/phase7_detuning_refinement.md` |

## Representative Results

Phase 6 uses equivalent parameterized geometry feedback:

```text
a_hot      = a_cold      + average inner-wall radial displacement
b_hot      = b_cold      + average outer-wall radial displacement
height_hot = height_cold + average top-wall axial displacement
```

For the maximum thermal-deformation case, the RF frequency shift is approximately:

```text
delta_f ~= -31.44 kHz
relative detuning ~= -2.1e-5
```

Phase 7 shows the detuning result is stable under RF mesh refinement:

```text
coarse: -31.4364 kHz
normal: -31.4358 kHz
fine:   -31.4357 kHz
```

The mesh-induced detuning spread is about `0.63 Hz`, so RF mesh resolution is not the dominant uncertainty in the current approximation.

## Important Boundary

This is not a full deformed-boundary RF solve.

The current thermal-detuning workflow uses an equivalent parameterized geometry approximation. It updates cavity geometry parameters from representative Phase 5 displacement metrics, then reruns the RF eigenfrequency problem.

A higher-fidelity future extension would map the full deformed boundary or deformed mesh into the RF model.

## Repository Contents

```text
docs/       Benchmark definition and project notes
reports/    Phase-by-phase technical reports
results/    Exported CSV tables and figures
tools/      COMSOL Java API scripts used for model generation and extraction
handoff/    Continuation notes for future work
```

## What Is Not Included

COMSOL model binaries are intentionally excluded:

```text
*.mph
*.mph.lock
*.mph.recovery
```

The repository also excludes COMSOL official application-library model files, official PDFs, and large third-party reference repositories. This avoids licensing ambiguity and keeps the Git repository reviewable.

## Reproducibility Notes

The Java scripts under `tools/` were run with COMSOL Multiphysics 6.4. They are intended as automation references for rebuilding the staged workflow in a local COMSOL installation.

CSV and PNG outputs are included so that the numerical validation trail remains visible even without committing proprietary binary model files.

