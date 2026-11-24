# Combat Particles (Fabric 1.21.11 + Sodium)

Adds a **Combat Particles** section to Sodium's Video Settings with one page per kit.
Pick the active kit yourself in the settings; there is no auto detection.

## What you see in Video Settings

- **Combat Particles** page: master on/off toggle, "Active profile" (Spear Mace / Mace / Crystal / SMP) and the size of the small totem.
- **Spear Mace**, **Mace**, **Crystal**, **SMP** pages: tick the particles you want to KEEP, everything else is removed.
  Also a **Totem animation** choice for when YOUR totem pops: Vanilla (big) / Small / Removed.

## Default profiles

| Profile    | Kept particles                                   | Totem animation (your own pop) |
|------------|--------------------------------------------------|--------------------------------|
| Spear Mace | totem pop + crit                                 | small                          |
| Mace       | totem pop + crit                                 | removed                        |
| Crystal    | totem pop                                        | small                          |
| SMP        | totem pop (everyone) + only MY crits + sweep arc | small                          |

Everything not listed is removed (all other particles, explosions, damage hearts, block break, etc.).
Every one of these can be changed in-game on each profile's page.

The small totem is a tiny totem icon above your crosshair (100% = hotbar item size, changeable 50-300%).
Totem pop particles are shown for every player; the totem animation is only ever for your own pop.

"Only my own hits" for crits works by remembering what you attacked: crit particles on that target within
0.6 seconds of your hit count as yours. If someone else crits the same target at the same moment it can slip through.

## Install

Needs: Minecraft 1.21.11, Fabric Loader, **Sodium 0.8.0+ for 1.21.11**.
Drop the jar into `.minecraft/mods`.

## Build the jar without using your PC (recommended for a weak PC)

1. Create a free GitHub account and a new empty repository.
2. Upload every file from this folder (keep the folder structure, including `.github/workflows/build.yml`).
3. Open the **Actions** tab, open the latest "build" run (or press "Run workflow").
4. When it finishes, download the **combatparticles-jar** artifact and unzip it. The jar inside is the mod.

## Build locally (needs about 2-3 GB free RAM)

Install JDK 21 and Gradle 9.x, then in this folder run:

    gradle build

The jar is in `build/libs/combatparticles-1.0.0.jar` (not the `-sources` one).

## Adding more kits later

Add an entry in `Profile.java`, a field + `switch` branch in `CombatConfig.java`,
and its defaults in `ProfileSettings.defaultsFor`. The settings pages are generated from the `Profile` list.

## Notes

- Config file: `config/combatparticles.json`
- Removing the totem animation only hides the picture. Totems still work and are used up as normal.
- Filtering applies to all particles you can see (other players', explosions, etc.), not just your own.
