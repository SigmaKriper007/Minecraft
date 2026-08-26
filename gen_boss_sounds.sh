#!/usr/bin/env bash
# gen_boss_sounds.sh — синтез звуков боя Омеги (задача 13).
# Все звуки — mono 44.1kHz OGG (Vorbis), генерация lavfi (aeval/anoisesrc).
set -e
cd "$(dirname "$0")"
OUT=src/main/resources/assets/opusvsexe/sounds
mkdir -p "$OUT"
R=44100

enc() { # outfile <- stdin raw pcm
  ffmpeg -y -hide_banner -loglevel error -f s16le -ar $R -ac 1 -i - \
    -af "alimiter=limit=0.95,loudnorm=I=-14:TP=-1.5:LRA=6" -c:a libvorbis -q:a 5 "$OUT/$1"
}

wave() { # name time expr-python (t локальная)
  python3 - "$2" "$3" <<'PY' > /tmp/wav_pcm.raw
import struct, sys, math, random
dur = float(sys.argv[1]); expr = sys.argv[2]
R = 44100
n = int(dur*R)
for i in range(n):
    t = i/R
    env = 1.0
    local = {'t': t, 'env': env, 'math': math, 'random': random}
    try:
        lines = [l for l in expr.split('\n') if l.strip()]
        for ln in lines[:-1]:
            exec(ln, local)
        x = eval(lines[-1], local)
    except Exception:
        x = 0
    x = max(-1.5, min(1.5, x))      # soft over; alimiter доведёт
    # мягкий клип
    if x > 1.0: x = 1.0 - 0.2/(x)   # rough soft
    elif x < -1.0: x = -1.0 + 0.2/(-x)
    sys.stdout.buffer.write(struct.pack('<h', int(max(-32767,min(32767, x*28000)))))
PY
  enc "$1" < /tmp/wav_pcm.raw
}

# -------- роар древнего ИИ (2.8s) --------
wave boss_roar.ogg 2.8 "
saw = lambda f: 2*(f*t % 1) - 1
f1 = 170 - 110*t/2.8
f2 = 85 - 55*t/2.8
vibr = 14*math.sin(2*math.pi*5.5*t)
a = 0.85*saw(f1) + 0.55*saw(f2 + vibr) + 0.25*math.sin(2*math.pi*(f1*0.5)*t)
# борода из шума
import random
a += 0.13*random.uniform(-1,1)*env
env = max(0.0, 1.0 - t/2.9)**0.7
a*env
"

# -------- шаг колосса (0.55s) --------
wave boss_step.ogg 0.55 "
env = math.exp(-6.5*t)
x = 0.95*math.sin(2*math.pi*(70 - 30*t)*t)*env
pat = math.exp(-40*max(0,t-0.02))*0.35*random.uniform(-1,1) if t>0.02 else 0
x + pat
"

# -------- удар кулаком (0.45s) --------
wave boss_punch.ogg 0.45 "
thud = math.sin(2*math.pi*90*t)*math.exp(-8*t)
metal = math.exp(-18*(t-0.03)**2*900)*0.8*math.sin(2*math.pi*720*t) if t>=0.03 else 0
noise = random.uniform(-1,1)*math.exp(-25*t)*0.35
x = 0.9*thud + 0.8*metal + noise
x
"

# -------- выстрел турели (0.4s) --------
wave boss_turret_shot.ogg 0.4 "
f = 1800*math.exp(-4.5*t) + 500
sq = 1 if math.sin(2*math.pi*f*t) > 0 else -1
env = math.exp(-4*t)
x = 0.7*sq*env + 0.25*random.uniform(-1,1)*env
x
"

# -------- предупреждение орбитального удара (1.3s, 3 бипа) --------
wave boss_orbital_warn.ogg 1.3 "
import math
period = 0.4
fr = [988, 1319, 1760][min(int(t/period),2)]
local = t % period
env_b = 1.0 if local < 0.22 else max(0.0, 1 - (local-0.22)/0.14)
x = 0.9*math.sin(2*math.pi*fr*t)*env_b
x
"

# -------- активация луча (1.5s) --------
wave boss_laser.ogg 1.5 "
f = 160*math.exp(1.25*t)
saw = 2*(f*t % 1) - 1
shimmer = 0.35*math.sin(2*math.pi*(f*3.01)*t + 7*math.sin(2*math.pi*13*t))
env = min(1.0, t/0.35)*math.exp(-0.9*t)
x = (0.6*saw + shimmer + 0.15*random.uniform(-1,1))*env
x
"

# -------- импульс кольца (1.4s) --------
wave boss_ring_burst.ogg 1.4 "
f = 90*math.exp(2.1*t)
env = 1.0 - t/1.45
sq = 1 if math.sin(2*math.pi*f*t) > 0 else -1
x = 0.75*sq*env + 0.3*math.sin(2*math.pi*(f*0.5)*t)*env + 0.2*random.uniform(-1,1)*env
x
"

# -------- телепорт/фазовый шаг (0.9s) --------
wave boss_teleport.ogg 0.9 "
f1 = 340 - 240*t
f2 = f1*1.013
beat = 0.5*math.sin(2*math.pi*f1*t) + 0.5*math.sin(2*math.pi*f2*t)
env = math.sin(math.pi*t/0.9)
x = beat*env + 0.18*random.uniform(-1,1)*env**2
x
"

# -------- попадание по ядру (0.35s) --------
wave boss_core_hit.ogg 0.35 "
p1 = math.sin(2*math.pi*1440*t)*0.6
p2 = math.sin(2*math.pi*2160*t)*0.35
p3 = math.sin(2*math.pi*720*t)*0.4
env = math.exp(-9*t)
x = (p1+p2+p3)*env
x
"

# -------- рикошет/отрицание (0.3s) --------
wave boss_deflect.ogg 0.3 "
fr = 3100*math.exp(-6*t)
ping = math.sin(2*math.pi*fr*t)*0.55
metal = random.uniform(-1,1)*math.exp(-35*t)*0.5 if t<0.15 else 0
env = 1.0
x = (ping + metal)*1.0
x
"

# -------- смена фазы (2.5s, riser + boom в конце) --------
wave boss_phase_shift.ogg 2.5 "
f = 70*math.exp(1.6*t)
saw = 2*((f*t) % 1) - 1
ris = min(1.0, t/2.2)*0.75
noise = (0.12 + 0.55*min(1.0,t/2.4)) * random.uniform(-1,1)
boom = 0.95*math.sin(2*math.pi*42*t)*math.exp(-4.5*(t-2.35)) if t>2.35 else 0
x = saw*ris + noise*0.5 + boom
x
"

# -------- гнев-шаг (1.6s, тяжёлый грохот) --------
wave boss_slam.ogg 1.6 "
thud = 0.95*math.sin(2*math.pi*45*t)*math.exp(-3.5*t)
rumble = random.uniform(-1,1)*0.5*math.exp(-2.0*t)
tab = max(0, 1.0 - abs(t-0.55)/0.25)
metal = 0.4*math.sin(2*math.pi*210*t)*tab
x = thud + rumble + metal
x
"

# -------- взрыв смерти (1.2s, как у эндер-дракона: boom + треск) --------
wave boss_explosion.ogg 1.2 "
boom = 0.95*math.sin(2*math.pi*(55 - 15*t)*t)*math.exp(-3.0*t)
crack = random.uniform(-1,1)*math.exp(-14*t)*0.7
low = 0.6*math.sin(2*math.pi*32*t)*math.exp(-2.2*t)
shock = 0.4*random.uniform(-1,1)*math.exp(-30*max(0,t-0.05)) if t>0.05 else 0
env = min(1.0, t/0.05)
x = (boom + crack + low + shock)*env
x
"

echo "закончено: $(ls -1 $OUT/boss_*.ogg | wc -l) файла boss_*.ogg"
