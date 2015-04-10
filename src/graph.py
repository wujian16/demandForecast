import cPickle as pickle
from collections import OrderedDict
import matplotlib.pyplot as plt
t=pickle.load(open("traffic.pickle", 'rb'))

sun={}
mon={}
tue={}
wed={}
thu={}
fri={}
sat={}

for key in t:
    if key[2]=='0':
        sun[key]=t[key]
    if key[2]=='1':
        mon[key]=t[key]
    if key[2]=='2':
        tue[key]=t[key]
    if key[2]=='3':
        wed[key]=t[key]
    if key[2]=='4':
        thu[key]=t[key]
    if key[2]=='5':
        fri[key]=t[key]
    if key[2]=='6':
        sat[key]=t[key]
for key in t:
    if len(key)==4:
        if key[3]=='0':
            sun[key]=t[key]
        if key[3]=='1':
            mon[key]=t[key]
        if key[3]=='2':
                tue[key]=t[key]
        if key[3]=='3':
            wed[key]=t[key]
        if key[3]=='4':
            thu[key]=t[key]
        if key[3]=='5':
            fri[key]=t[key]
        if key[3]=='6':
            sat[key]=t[key]

mono=OrderedDict(sorted(mon.items(),key=lambda t:t[0]))
suno=OrderedDict(sorted(sun.items(),key=lambda t:t[0]))
tueo=OrderedDict(sorted(tue.items(),key=lambda t:t[0]))
wedo=OrderedDict(sorted(wed.items(),key=lambda t:t[0]))
thuo=OrderedDict(sorted(thu.items(),key=lambda t:t[0]))
frio=OrderedDict(sorted(fri.items(),key=lambda t:t[0]))
sato=OrderedDict(sorted(sat.items(),key=lambda t:t[0]))

l=[]

for key in suno:
    if len(key)==3:
        l.append(suno[key])
for key in suno:
    if len(key)==4:
        l.append(suno[key])
for key in mono:
    if len(key)==3:
        l.append(mono[key])
for key in mono:
    if len(key)==4:
        l.append(mono[key])
for key in tueo:
    if len(key)==3:
        l.append(tueo[key])
for key in tueo:
    if len(key)==4:
        l.append(tueo[key])
for key in wedo:
    if len(key)==3:
        l.append(wedo[key])
for key in wedo:
    if len(key)==4:
        l.append(wedo[key])
for key in thuo:
    if len(key)==3:
        l.append(thuo[key])
for key in thuo:
    if len(key)==4:
        l.append(thuo[key])
for key in frio:
    if len(key)==3:
        l.append(frio[key])
for key in frio:
    if len(key)==4:
        l.append(frio[key])
for key in sato:
    if len(key)==3:
        l.append(sato[key])
for key in sato:
    if len(key)==4:
        l.append(sato[key])
plt.plot(l)
plt.show()