# Validation Logic

This note collects the formulas and sanity checks used across the RF cavity thermal-detuning benchmark.

## Ideal Pillbox TM010 Frequency

For an ideal cylindrical pillbox cavity, the TM010 frequency can be estimated by:

```text
f010 = x01 * c / (2*pi*R)
```

where:

- `x01 ~= 2.4048255577` is the first zero of the Bessel function `J0`.
- `c` is the speed of light.
- `R` is the ideal cavity radius.

This formula is used as an analytical reference for understanding frequency scale. The project's formal Phase 1 benchmark uses the COMSOL RF eigenfrequency solution and compares the first physical mode against the COMSOL reference result.

## Copper Surface Resistance

For a good conductor under RF excitation, the surface resistance can be estimated as:

```text
Rs = sqrt(pi * f * mu0 / sigma)
```

where:

- `f` is RF frequency.
- `mu0` is vacuum permeability.
- `sigma` is electrical conductivity.

For copper, this gives the wall-loss conversion factor used in the field-derived RF heating step.

## Surface Loss To Heat Flux

The time-averaged conductor surface loss is estimated as:

```text
q = 0.5 * Rs * |H_t|^2
```

Unit check:

```text
Rs: ohm = V/A
H_t: A/m
Rs * |H_t|^2 = (V/A) * (A^2/m^2) = V*A/m^2 = W/m^2
```

The factor `0.5` is the time-average factor for harmonic fields when using peak field magnitude.

In this project, eigenmode amplitude is arbitrary, so this expression is used for the wall-loss distribution shape and then normalized to prescribed total-power cases.

## Heat Balance Check

For a 2D axisymmetric model, line integrals are weighted by `2*pi*r`.

Input wall-loss power:

```text
P_in = integral_inner_wall(2*pi*r*q_wall)
```

Convective heat removal:

```text
P_out = integral_cooling_boundaries(2*pi*r*h*(T - T_amb))
```

At steady state:

```text
P_in ~= P_out
```

The RF-heated thermal sweep closes this balance with residual below `1e-8 W`.

## Thermal Expansion Scale

A first-order displacement estimate is:

```text
delta_L ~= alpha * DeltaT * L
```

where:

- `alpha` is the coefficient of thermal expansion.
- `DeltaT` is the representative temperature rise.
- `L` is a representative length scale.

The structural benchmark compares simulated displacement against this scale rather than treating raw displacement values as self-validating.

## Detuning Calculation

Thermal detuning is calculated by comparing RF eigenfrequency before and after equivalent geometry feedback:

```text
delta_f = f_hot - f_cold
relative_detuning = delta_f / f_cold
```

In Phase 6/7:

```text
a_hot      = a_cold      + average inner-wall radial displacement
b_hot      = b_cold      + average outer-wall radial displacement
height_hot = height_cold + average top-wall axial displacement
```

This is an equivalent parameterized-geometry approximation. It is not a full deformed-boundary RF solve.

## Mesh Sensitivity

Mesh sensitivity checks whether a reported quantity changes materially when the finite-element mesh is refined.

In Phase 7, the cold/hot RF detuning comparison was repeated with:

```text
coarse mesh
normal mesh
fine mesh
```

The resulting detuning values differed by about `0.63 Hz` over a `31 kHz`-level shift. This supports the conclusion that RF mesh resolution is not the dominant uncertainty inside the current equivalent-geometry approximation.

