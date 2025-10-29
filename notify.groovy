def call(String prefix, String botToken, String chatId) {
  def jobName = env.JOB_NAME ?: "Unknown job"
  def buildStatus = currentBuild.currentResult ?: "UNKNOWN"
  def buildUrl = env.BUILD_URL ?: "None"

  def msg = "Build ${jobName} #${buildStatus} ${prefix} Job Link: ${buildUrl}"

  sh """
    curl -s -X POST https://api.telegram.org/bot${botToken}/sendMessage \
    -d chat_id=${chatId} \
    -d parse_mode=Markdown \
    -d text="${msg}"
  """
}
