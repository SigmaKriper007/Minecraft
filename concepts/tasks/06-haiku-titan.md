# Задача 6 — Haiku-5 Titan Frame (мини-босс, колосс-мех)

## Референсы (MASTER_PLAN §5.6, §6)
- Роль: колоссальный человекоподобный мех ~9 блоков, HP 200, урон 15, броня 14, KB 0.7. Hitbox 3.0×9.0.
- Промт: маленькая голова-шлем с гребнем (контраст масштаба); широкая грудь с ОГРОМНЫМ янтарным реактором; двойные наплечники; правая рука-пушка (крупный излучатель), левая — щит-клешня; массивные ноги с коленными щитками; кольца-орбы на суставах; «ожерелье» из янтарных сегментов; заметный износ — заплатки, потёки.

## Реализация (2026-08-21)
- `generate_titan_entity.py` (переиспользует художников Enforcer/Warden): 17 костей/29 кубов, атлас 128×128, 6 анимаций (idle/walk/attack/SPECIAL/hurt/death), фронт −Z, формат 1.12.0. Новые стили: `titan_front` (кираса+заплатки+потёки), `titan_dark`, `giant_core`, `amber_seg`, `claw`. Кости-якоря: `reactor`, `cannon_r` (отдача), `claw_l` (замах), `knee_l/r` (сгиб в walk).
- Java: `TitanModel`, `TitanRenderer` (BASE 1.875 → scale 9.0 ≈ 4.8); `Haiku5Entity` — контроллер `titan_controller`; special с шансом 30% при ударе (`specialUntil`).
- Дроп: **Core Opus** (Haiku5Entity.die → CORE_OPUS ×1).
- ДОЛГ ЗАКРЫТ (из задачи 5): Warden дропает **Resonant Opus**.
- Регистрация: HAIKU_5 → TitanRenderer.
- QA: DANGER 0, миссов 0.

## Открытые вопросы (в задачу 7+)
- [ ] Дроп Omega (HaikuOmegaEntity) — Core Opus ×3 / Omega Frame.