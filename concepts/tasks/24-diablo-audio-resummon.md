# Task 24 — Diablo battle audio and repeat summon

## Design

- `diablo_theme` is a streamed, looping music event played client-relative with no attenuation while an awakened Diablo has a living target.
- Server owns the engagement state and explicitly starts/stops the client loop. Joining the dimension mid-fight works; losing the target, leaving the realm or killing Diablo stops it.
- Spoken combat lines remain `playNotifySound` events to every player in the Fire Realm, so their loudness never changes with distance and they layer over music.
- After Diablo dies, right-clicking the central Seal Cryoice with four Vein Essences rebuilds the prison and creates one sealed Diablo. The ritual refuses while any living Diablo is in the arena and never duplicates the realm.

## QA

- Sound event/file/stream references validate; theme is distinct from realm ambience.
- Audio start/stop is server-authoritative and cannot remain stuck after target loss/death/dimension exit.
- Repeat ritual is server-only, consumes exactly four essence outside creative and cannot create two bosses.
- Build, JSON/audio validation, dedicated startup and two-attempt summon test.

## Result — 2026-08-25

- Supplied `diablo_theme.mp3` converted to a 152-second mono 44.1 kHz Vorbis stream and registered with localized subtitles.
- Server engagement state starts/stops a relative no-attenuation client music loop; realm ambience yields during the fight. Combat phrases and death voice notify every player in the dimension.
- Central Seal Cryoice accepts four Vein Essences after victory, reconstructs the prison and creates exactly one sealed Diablo. Existing living bosses reject the ritual.
- Diablo now sets persistence and overrides distance despawn. Runtime testing confirmed the boss remains present in a loaded playerless chunk; test force-load state and entity were cleaned up.
- EN/RU/UK/PL/DE strings, all JSON, audio metadata, full build and dedicated startup passed. Live audio and right-click ritual input remain manual client QA.
