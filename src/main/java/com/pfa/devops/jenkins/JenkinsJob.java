package com.pfa.devops.jenkins;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JenkinsJob {

	public static final String JENKINS_URI = "http://localhost:8080";
	public static final String USERNAME = "firstAdmin";
	public static final String PASSWORD = "1191e8798b94ac79434a686b0070a4c956";   //password should be token api, otherwise crumb error 401

	private String job_name;
	private String xml_job_config_path = "src/main/java/com/pfa/devops/jenkins/job1.xml";

	public JenkinsJob(String job_name, String xml_job_config_path) {
		this.job_name = job_name;
		this.xml_job_config_path = xml_job_config_path;

	}

	public void createJob(){
		try {
			createJob(JENKINS_URI, job_name, xml_job_config_path);
		} catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}


	public static void createParametrizedBuildGitFlag(String param1, String param2, String jobName){
		JenkinsClient client = JenkinsClient.builder()
				.endPoint(JENKINS_URI) //
				.credentials(USERNAME+":"+PASSWORD) // Optional.
				.build();
		SystemInfo systemInfo = client.api().systemApi().systemInfo();
		System.out.println(systemInfo);

		// set parameters
		Map<String, List<String>> params = new HashMap<>();
		List<String> paramList1 = new ArrayList<>();
		List<String> paramList2 = new ArrayList<>();

		paramList1.add(param1);
		paramList2.add(param2);
		params.put("GITHUB", paramList1);
		params.put("FLAG", paramList2);

// sending request
		IntegerResponse response = client.api().jobsApi()
				.buildWithParameters(null, jobName, params);
	}

	public static List<String> listJobs() {
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(JENKINS_URI + "/api/xml");
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();

		List<String> jobList = new ArrayList<>();
		String[] jobs = jsonResponse.split("job>");
		for (String job : jobs) {
			String[] names = job.split("name>");
			if (names.length == 3) {
				String name = names[1];
				name = name.substring(0, name.length() - 2); // Take off </ for the closing name tag: </name>
				jobList.add(name);

			}

		}
		return jobList;
	}
	public List<CustomPair> getArtifactUrl(String job_name){
		List<CustomPair> pairList = new ArrayList<>();
		try {
			JenkinsServer jenkins = new JenkinsServer(new URI(JENKINS_URI), USERNAME, PASSWORD);
			Map<String, Job> jobs = jenkins.getJobs();
			JobWithDetails jobWithDetails = jobs.get(job_name).details();
			//System.out.println(jobWithDetails.getLastSuccessfulBuild().details().getConsoleOutputText());
			Build build = jobWithDetails.getLastSuccessfulBuild();
			List<Artifact> 	artifacts = build.details().getArtifacts();
			for (Artifact art : artifacts){
				System.out.println(art.getFileName());
				System.out.println(art.getDisplayPath());
				System.out.println(art.getRelativePath());
				System.out.println("downloading");
				InputStream inputStream = build.details().downloadArtifact(art);
				URI uri = new URI(build.getUrl());
				String artifactPath = uri.getPath() + "artifact/" + art.getRelativePath();
				URI artifactUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), artifactPath, "", "");
				System.out.println(artifactUri);
				pairList.add(new CustomPair(art.getFileName(), artifactUri.toString()));
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}

		return pairList;
	}
	private boolean areErrors(List<Error> errors) {
		int num_errors = 0;
		for (Error error : errors) {
			num_errors++;
			System.out.println("error : " + error.exceptionName());
		}

		return num_errors > 0;
	}

	private String xmlToString(Document document) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		StringWriter sw = new StringWriter();
		t.transform(new DOMSource(document), new StreamResult(sw));


		return sw.toString();
	}

	private String createJob(String url, String newJobName, String xmlFileUri) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = documentBuilder.newDocumentBuilder();
		Document doc = docBuilder.parse(xmlFileUri);

		String xmlString = xmlToString(doc);
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(url + "/createItem?name=" + newJobName);
		ClientResponse response = webResource.type("application/xml").post(ClientResponse.class, xmlString);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		//System.out.println("Response createJob:::::" + jsonResponse);

		return jsonResponse;

	}

	//https://github.com/jenkinsci/java-client-api
	public JobWithDetails details() throws IOException, URISyntaxException {

		JenkinsServer jenkins = new JenkinsServer(new URI(JENKINS_URI), USERNAME, PASSWORD);

		Map<String, Job> jobs = jenkins.getJobs();
		JobWithDetails job;
		//job.getLastSuccessfulBuild().details().getConsoleOutputText();
		return jobs.get(job_name).details();

	}

	public String deleteJob() {
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(JENKINS_URI + "/job/" + job_name + "/doDelete");
		ClientResponse response = webResource.post(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		//System.out.println("Response deleteJobs:::::" + jsonResponse);

		return jsonResponse;
	}

	public String buildJob() throws InterruptedException {
		JenkinsClient client = JenkinsClient.builder()
				.endPoint(JENKINS_URI) //
				.credentials(USERNAME + ":" + PASSWORD)
				.build();
		IntegerResponse queueId = client.api().jobsApi().build(null, job_name);
		List<Error> errorList = queueId.errors();
		if (!areErrors(errorList))
			System.out.println("Build successfuly submitted with queue id: " + queueId.value());

		QueueItem queueItem = client.api().queueApi().queueItem(queueId.value());
		while (true) {
			if (queueItem.cancelled())
				System.out.println("Queue item cancelled");
			if (queueItem.executable() != null) {
				System.out.println("Build is executing with build number: " + queueItem.executable().number());
				break;
			}
			Thread.sleep(10000);
			queueItem = client.api().queueApi().queueItem(queueId.value());
		}
		BuildInfo buildInfo = client.api().jobsApi().buildInfo(null, job_name, queueItem.executable().number());
		while (buildInfo.result() == null) {
			Thread.sleep(10000);
			buildInfo = client.api().jobsApi().buildInfo(null, job_name, queueItem.executable().number());
		}
		//System.out.println("Build status : " + buildInfo.result());

		return buildInfo.result();

	}

	public String readJob() {
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(JENKINS_URI + "/job/" + job_name + "/config.xml");
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
//		System.out.println("Response readJob:::::"+jsonResponse);
		return jsonResponse;
	}

	public String crumbCredentialGetter() {
		Client client = Client.create();

		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));

		WebResource webResource = client
				.resource(JENKINS_URI + "/crumbIssuer/api/json");

		ClientResponse response = webResource.accept("application/json")
				.get(ClientResponse.class);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}
		String output = response.getEntity(String.class);
		System.out.println("Output from Server .... \n");
		System.out.println(output);

		return output.split(",")[1];
	}

	public String getJob_name() {
		return job_name;
	}

	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}

	public String getXml_job_config_path() {
		return xml_job_config_path;
	}

	public void setXml_job_config_path(String xml_job_config_path) {
		this.xml_job_config_path = xml_job_config_path;
	}

}

