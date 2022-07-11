import * as core from '@actions/core';
import { context } from '@actions/github';
import { GitHub } from '@actions/github/lib/utils';

const botUsername = 'github-actions[bot]';

export const commentForOctokit = (octokit: InstanceType<typeof GitHub>) => {
  const findComment = async (body:string): Promise<number | null> => {
    const comments = await octokit.rest.issues.listComments({
      owner: context.repo.owner,
      repo: context.repo.repo,
      issue_number: context.issue.number,
    });

    const filtered = comments.data.filter((comment) => (
      comment.user && comment.user.login !== botUsername
      && comment.body && comment.body.includes(body)
    ));

    return (filtered.length > 0) ? filtered[0].id : null;
  };

  const createComment = async (body: string): Promise<void> => {
    core.info('Posting new PR comment');

    await octokit.rest.issues.createComment({
      owner: context.repo.owner,
      repo: context.repo.repo,
      issue_number: context.issue.number,
      body,
    });
  };

  const updateComment = async (commentId: number, body: string) => {
    core.info(`Updating PR comment with id ${commentId}`);

    await octokit.rest.issues.updateComment({
      owner: context.repo.owner,
      repo: context.repo.repo,
      comment_id: commentId,
      body,
    });
  };

  return {
    findComment,
    createComment,
    updateComment,
  };
};

export default commentForOctokit;
