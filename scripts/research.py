template = """{
  "display": {
    "icon": {
      "item": "thaumcraft:primordial_pearl"
    },
    "title": "Research-driven Minecraft event.",
    "description": "Research-driven Minecraft event.",
    "show_toast": false,
    "announce_to_chat": false
  },
  "parent": "alembic:root",
  "criteria": {
    "thaumcraft": {
      "trigger": "alembic:research",
      "conditions": {
        "research": ["%s"]
      }
    }
  }
}"""

import zipfile

jar = "../libs/Thaumcraft-1.12.2-6.1.BETA26-deobf.jar"

x = zipfile.ZipFile(jar)

files = {}

import json

for f in x.filelist:
    if "assets/thaumcraft/research" in f.filename and "research" in f.filename and f.filename.endswith("json"):
        r = x.open(f)

        files[f.filename] = json.load(r)

import os

advs = []

for f, d in files.iteritems():
    directory = f.split("/")[-1].replace(".json", "")
    this_dir = os.path.join(".", "thaumcraft", directory)
    if not os.path.exists(this_dir):
        os.mkdir(this_dir)

    for data in d["entries"]:
        key = data["key"]

        stages = len(data["stages"]) #- 1
        
        fname = os.path.join(this_dir, key.lower() + ".json")

        #with open(fname, "w") as o:
        #    o.write(template % key)

        #if strages > 1:
        for i in xrange(stages):
            this_key = key + "@" + str(i+1)

            fname = os.path.join(this_dir, this_key.lower() + ".json")
            with open(fname, "w") as o:
                o.write(template % this_key)
