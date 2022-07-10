import * as core from '@actions/core';
import * as artifact from '@actions/artifact';
import { readdir } from 'fs/promises';
import path from 'path';
// import commentForOctokit from './comment';

const getAlbatrouzFiles = async (dir: string): Promise<string[]> => {
  const files = await readdir(dir);
  const apiFiles = await readdir(path.join(dir, 'Apis'));
  return files.concat(apiFiles).filter((f) => f.endsWith('.yaml') || f.endsWith('.tilt'));
};

async function run(): Promise<void> {
  // const commitHash = core.getInput('commit', { required: true });
  // const repoToken = core.getInput('repo-token', { required: true });
  const albatrouzOutDir = core.getInput('albatrouz-out', { required: true });

  // const octokit = getOctokit(repoToken);
  // const comments = commentForOctokit(octokit);

  const artifacts = artifact.create();
  const albatrouzFiles = await getAlbatrouzFiles(albatrouzOutDir);
  const responses = await Promise.all(albatrouzFiles.map((f) => (
    artifacts.uploadArtifact(path.basename(f), [f], albatrouzOutDir)
  )));

  responses.forEach((uploadResponse) => {
    if (uploadResponse.failedItems.length > 0) {
      core.warning(`Upload of artifact ${uploadResponse.artifactName} failed.`);
    }
  });
}

run();
