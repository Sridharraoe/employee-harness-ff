const file1 = "flags.yaml";
const environment = "develop";
const yaml = require('yaml');
const axios = require('axios');
const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
let yamlDataFile2;

//constants from pipeline env variables
const gitToken = process.argv[2];
const flagPipelineUrl = process.argv[3];
const repoUrl = process.argv[4];
const mainBranch = process.argv[5];

if (!gitToken) {
    console.error("Error: GIT_TOKEN_MAIN_BRANCH is required.");
    process.exit(1);
}

if (!repoUrl) {
      console.error("Error: GIT_REPO_URL_MAIN_BRANCH pipeline variable not set");
      process.exit(1);
  }

if (!flagPipelineUrl) {
     console.error("Error: FLAG_UPDATE_PIPELINE_URL pipeline variable not set");
     process.exit(1);
 }

 if (!mainBranch) {
     console.error("Error: GIT_REPO_MAIN_BRANCH_NAME pipeline variable not set");
     process.exit(1);
 }


function fetchYamlFileFromGit(repoUrl, filePath, localPath, token, branch) {
    if (fs.existsSync(localPath)) {
        fs.rmdirSync(localPath, { recursive: true });
    }
    const urlWithToken = repoUrl.replace('https://', `https://${token}@`);
    execSync(`git clone -b ${branch} ${urlWithToken} ${localPath} --depth 1`, { stdio: 'inherit' });
    let fileContent;

    try {
        fileContent = fs.readFileSync(path.join(localPath, filePath), 'utf8');
    } catch (error) {
        console.error("Error reading file:", error.message);
        process.exit(1);
    }
    yamlDataFile2 = yaml.parse(fileContent);
    fs.rm(localPath, { recursive: true }, (err) => {
        if (err) {
            console.error("Error removing directory:", err);
        } else {
            console.log("Directory removed successfully");
        }
    });
}
const mainBranchFilePath = 'flags.yaml';
const localPath = 'temp-repo';

try {
  fetchYamlFileFromGit(repoUrl, mainBranchFilePath, localPath, gitToken, mainBranch);
  //console.log(yamlDataFile2); // Print the YAML data

  const doc1 = yaml.parse(fs.readFileSync(file1, 'utf8'));
  const doc2 = yamlDataFile2;

  const featureFlagsInDoc1 = doc1.featureFlags.flags;
  const featureFlagsInDoc2 = doc2.featureFlags.flags;

  let newAndCommonFlags = [];

  console.log("For feature flag set 1");

  for (let i = 0; i < featureFlagsInDoc1.length; i++) {
    const flag1 = featureFlagsInDoc1[i];

    const commonFlagIndex = featureFlagsInDoc2.findIndex(feature => {
      return (
        flag1.flag.name === feature.flag.name
      );
    });

    if (commonFlagIndex !== -1) {
      newAndCommonFlags.push({ flags: [flag1, featureFlagsInDoc2[commonFlagIndex]], common: true });
    } else {
      newAndCommonFlags.push({ flags: [flag1], common: false });
    }
  }

  console.log("For feature flag set 2");

  for (let j = 0; j < featureFlagsInDoc2.length; j++) {
    const flag2 = featureFlagsInDoc2[j];

    const commonFlagIndex = featureFlagsInDoc1.findIndex(feature => {
      return flag2.flag.name === feature.flag.name;
    });

    if( commonFlagIndex==-1){
     //   newAndCommonFlags.push({flags:[featureFlagsInDoc1[commonFlagIndex],flag2],common:true});
      }else /*not found in flags2*/{
          newAndCommonFlags.push({flags:[flag2],common:false});
      }
  }
 //console.log(JSON.stringify(newAndCommonFlags, null, 2));
  console.log("Starting foreach");

  newAndCommonFlags.forEach(newAndCommonFlag => {
    if (newAndCommonFlag.common) {
      const flag1 = newAndCommonFlag.flags[0].flag;
      const flag2 = newAndCommonFlag.flags[1].flag;

      const valueInDevelopForFlag1 = flag1.environments.find(env => env.identifier === environment).state;
      const valueInDevelopForFlag2 = flag2.environments.find(env => env.identifier === environment).state;

      if (valueInDevelopForFlag1 !== valueInDevelopForFlag2) {
        console.log(`Flag ${flag1.name} has changed to: ${valueInDevelopForFlag1}`);
        const data = {
          flagName: flag1.name,
          flagSwitch: valueInDevelopForFlag1,
        };

        const config = {
          headers: {
            'Content-Type': 'application/json',
          },
        };

        // Send feature flag pipeline request if value has changed
           axios.post(flagPipelineUrl, data, config)
           .then(response => {
             console.log(`Feature flag pipeline started for: ${flag1.name}`);
             console.log(response.data);
           })
           .catch(error => {
             console.error(error);
           });
      }
    } else {
      const newFlag = newAndCommonFlag.flags[0].flag;
      const valueInDevelopForNewFlag = newFlag.environments.find(env => env.identifier === environment).state;

      //console.log(`New: ${newFlag.name} flag with value: ${valueInDevelopForNewFlag}`);
    }
  });

  console.log("Done with foreach");
} catch (e) {
  console.log(e);
  process.exit(1);
}