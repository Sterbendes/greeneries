# Automatic platform-specific conversions

This template can automatically convert some platform-specific
parts of datapacks such as fluid units and data loading conditions
from a defined common format to the platform-specific formats.

If you want to see how this is used in practice, check out
the [Create compatibility recipes in the VeganDelight mod](https://github.com/SayWhatSayMon/VeganDelight/tree/1.21.1/multiloader/common/src/main/resources/data/vegandelight/recipe/integration/create)

## How to enable:
In the `gradle.properties` file, set the `convert_fluid_units` or
`unified_load_conditions` property to `true`

## Fluid unit conversion
Fabric usually uses droplets as a unit for fluids,
where 1 bucket ≙ 81,000 droplets,
while neoforge uses the traditional millibucket (mB) system, where
1 bucket ≙ 1,000 mB

When enabled, this template can automatically convert fluid units to
the correct one when specified like this:
```json5
{
  // ...
  "amount": "10_millibuckets",
  // ...
}
```
This will be converted to `"amount": 10` in the neoforge jar, and to
`"amount": 810` in the fabric jar (which is the same amount, but
converted from mB to droplets)

You can also do it the other way around:
```json5
{
  // ...
  "amount": "81000_droplets",
  // ...
}
```
Which will be converted to `"amount": 1000` in the neoforge jar, and
to `"amount": 81000` in the fabric jar.

## Loading condition format
Fabric and neoforge both have a system to allow resources to only
be loaded when certain conditions are loaded, they, however use
a slightly different syntax.

When enabled, this template will fix that by providing a common syntax
that will be automatically mapped to the platform-specific syntax at build time.
```json5
{
  "load_conditions": [
    // conditions go here
  ]
  // rest of recipe definition
}
```
The `load_conditions` part will be changed to
`fabric:load_conditions` in the fabric jar and to
`neoforge:conditions` in the neoforge jar.

### Supported conditions

**Detect if other mod is loaded**

This can be used to only load a resource if a certain other mod is
installed.

Example:
```json5
{
  "load_conditions": [
    {
      "condition": "mod_loaded",
      "modid": "create" // the id of the mod you want to check
    }
  ]
  // ...
}
```
This will cause the recipe to only be loaded if the Create mod is
installed.

**Loader detection**

This can be used to only load certain resources on one loader.

Example:
```json5
{
  "load_conditions": [
    {
      "condition": "is_neoforge"
    }
  ]
  // ...
}
```
This will only load the recipe on neoforge.
Change `"is_neoforge"` to `"is_fabric"` to only load the recipe on
fabric instead.

**Inverting conditions**

To invert a condition, just put `"not": { ... }` around the condition

Example:
```json5
{
  "load_conditions": [
    {
      "not": {
        // the condition you want to invert
        "condition": "mod_loaded",
        "mod": "create"
      }
    }
  ]
  // ...
}
```
This would only load the recipe if the Create mod is not installed

**Logical or/logical and**

Example:
```json5
{
  "load_conditions": [
    {
      "condition": "or",
      "values": [
        // put the conditions you want to be linked using 'or' here
      ]
    }
  ]
  // ...
}
```
For an and-condition, just change "or" to "and".
