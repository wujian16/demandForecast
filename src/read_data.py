import simplejson as json
from pprint import pprint
import matplotlib.pyplot as plt
import cPickle as pickle

column_names = set()
business_id=[]
with open('business.json') as fin:
    for line in fin:
        data = json.loads(line)
        if data['city']=="Phoenix":
            business_id.append(data['business_id'])


#print business_id
traffic={}
with open('checkin.json') as f:
    for line in f:
        data = json.loads(line)
        if data['business_id'] in business_id:
            for key in data['checkin_info']:
                if key in traffic:
                    traffic[key]=traffic[key]+data['checkin_info'][key]
                else:
                    traffic[key]=data['checkin_info'][key]

pickle.dumps(traffic, open("traffic.pickle", 'wb'))

sun={}
mon={]
tue={}
wed={}
thu={}
fri={}
sat={}

for key in traffic:
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
for key in traffic:
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





