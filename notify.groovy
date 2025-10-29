def call(String prefix, String botToken, String chatid) {
  def jobName = env.JOB_NAME ?: "Unknown job"
  def buildStatus = currentBuild.currentResult ?: "UNKNOWN"
  def buildUrl = env.BUILD_URL ?: "None"

  def tmpl = readFile('notifyTmpl.md')

  def msg = tmpl
        .replace('${prefix}', prefix)
        .replace('${jobName}', jobName)
        .replace('${buildStatus}', buildStatus)
        .replace('buildUrl}', buildUrl)

  sh """
    curl -s -X POST https://api.telegram.org/bot${botToken}/sendMessage \
    -d chat_id=${chatId} \
    -d parse_mode=Markdown \
    -d text="${msg}"
  """
}
