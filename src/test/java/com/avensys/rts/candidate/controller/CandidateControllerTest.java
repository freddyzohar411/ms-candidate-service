package com.avensys.rts.candidate.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.avensys.rts.candidate.payloadnewrequest.CandidateListingRequestDTO;
import com.avensys.rts.candidate.payloadnewrequest.CandidateRequestDTO;
import com.avensys.rts.candidate.service.CandidateServiceImpl;
import com.avensys.rts.candidate.util.JwtUtil;
import com.avensys.rts.candidate.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@ExtendWith(MockitoExtension.class)
public class CandidateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	AutoCloseable autoCloseable;

	@InjectMocks
	CandidateController candidateController;
	
	CandidateListingRequestDTO candidateListingRequestDTO;
	
	@Mock
	JwtUtil jwtUtil;
	
	@Mock
	MessageSource messageSource;
	
	@Mock
	UserUtil userUtil;
	
	@Mock
	CandidateServiceImpl candidateServiceImpl;
	
	List<String> searchFields;
	
	CandidateRequestDTO candidateRequestDTO;
	
	String formData = "{\"id\":1,\"accountSubmissionData\":{\"msa\":\"yes\",\"revenue\":32432434,\"website\":\"www.tcs.com\",\"industry\":\"InformationTechnology\",\"salesName\":\"Test\",\"leadSource\":\"Test\",\"accountName\":\"TCS\",\"addressCity\":\"\",\"billingCity\":\"\",\"subIndustry\":\"SoftwareDevelopment\",\"addressLine1\":\"Bhopal\",\"addressLine2\":\"\",\"addressLine3\":\"\",\"accountRating\":\"Tier1\",\"accountSource\":\"TalentService\",\"accountStatus\":\"Active\",\"leadSalesName\":\"Test\",\"noOfEmployees\":6,\"parentCompany\":\"\",\"accountRemarks\":\"\",\"addressCountry\":\"\",\"billingAddress\":\"true\",\"landlineNumber\":324,\"secondaryOwner\":\"Test\",\"landlineCountry\":\"\",\"leadAccountName\":\"TCS\",\"revenueCurrency\":\"INR₹\",\"uploadAgreement\":\"Reema_Sahu_Java_5Yrs.docx(1).pdf\",\"addressPostalCode\":\"\",\"billingAddressLine1\":\"Bhopal\",\"billingAddressLine2\":\"\",\"billingAddressLine3\":\"\",\"billingAddressCountry\":\"\",\"billingAddressPostalCode\":\"\"},\"commercialSubmissionData\":{\"msp\":\"Test\",\"markUp\":\"Test\"},\"accountNumber\":\"A0958950\",\"createdAt\":\"2024-01-16T13:02:13.006307\",\"updatedAt\":\"2024-01-16T13:06:15.374175\",\"accountCountry\":\"India\",\"createdByName\":\"Super1Admin1\",\"updatedByName\":\"Super1Admin1\"}";

	
	@BeforeEach
	public void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		candidateListingRequestDTO = new CandidateListingRequestDTO(0,5,"sortBy","sortDirection","searchTerm",searchFields, 1L,"customQuery", true);
		candidateRequestDTO = new CandidateRequestDTO("kotaiah","nalleboina","kotaiah@gmail.com",formData,1);
		this.mockMvc = MockMvcBuilders.standaloneSetup(candidateController).build();
	}

	@AfterEach
	public void tearDown() throws Exception {
		autoCloseable.close();
	}
	
	@Test
	void testAddCandidatePositive() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/candidates/add")
				.content(asJsonString( new CandidateRequestDTO("kotaiah","nalleboina","kotaiah@gmail.com",formData,1)))
				.header("Authorization",
						"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0WndUaGhXVUtGSjhUdE1NdFZrcm1Edk9TdGdRcS1Sa3MwUnEwRE5IRG5jIn0.eyJleHAiOjE3MDMyMzI3MTQsImlhdCI6MTcwMzIzMjQxNCwianRpIjoiNmMwYjBlMmYtMDZmYi00YzU3LWJmMWQtM2MzNmEzZGUxOGQxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9ydHNyZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzMzlmMzVhNy0wZDNkLTQzMWUtOWE2My1kOTBkNGMzNDJlNGEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJydHNjbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3d3dy5rZXljbG9hay5vcmciXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJ0c3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJLb3RhaSBOYWxsZWIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJraXR0dTFAYXZlbi1zeXMuY29tIiwiZ2l2ZW5fbmFtZSI6IktvdGFpIiwiZmFtaWx5X25hbWUiOiJOYWxsZWIiLCJlbWFpbCI6ImtpdHR1MUBhdmVuLXN5cy5jb20ifQ.A314CP_nu6x3qENsK8fyZP8SXXJO9y1nAcUXHU2FRRZ2vtPjD-T6rUoHQ_CZgMXnPg4Rl4MOlSCQ5leTiWix9kfBYkDQGar7GPSf9UnnPai7adiLV8Rb6OUYykHPjN_Wy3A0CVyGbsBB1ow7uhmgPkM7aMBUUYikkYK0aLremKn9vXJCpC7G2UTCW_BOjl7Bb5atic3J328ieN8nu0_W_Zd61ux1zm7skX4TPLNTC-4dAc16O-6IOo6JChQLUublfm-CcVC_i7oIv0Nuw7hOj5m5_e0klNcK-dw9bArBkRCGU9Sr4ieFIkjaLxt22Z3ZDg0C9SeB268OvnKXrjDKiQ")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isCreated());
	}

	/**
	 * This method is used to convert Json object to string.
	 * 
	 * @param obj
	 * @return
	 */
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	void testGetCandidate()throws Exception  {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/candidates/{id}", 1))
				.andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testUpdateCandidate()throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(candidateRequestDTO);
		RequestBuilder request = MockMvcRequestBuilders.put("/api/candidates/{id}", 1).content(requestJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testDeleteCandidate()throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/candidates/draft/{id}",1L))
				.andExpect(status().isOk()).andReturn();

	}
	
	@Test
	void testCompleteCandidateCreate()throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/candidates/{id}/complete",1L))
				.andExpect(status().isOk()).andReturn();

	}
	
	@Test
	void testGetCandidateIfDraft() throws Exception{
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/candidates/draft", 1))
				.andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testSoftDeleteCandidate()throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/candidates/{id}", 1))
				.andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testGetAllCandidatesFields()throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/candidates/fields", 1))
				.andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testGetCandidateListing()throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(candidateListingRequestDTO);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/candidates/listing/similarity-search").content(requestJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testGetCandidateListingAll()throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(candidateListingRequestDTO);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/candidates/listing/all").content(requestJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testGetCandidateByIdData()throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/candidates/{candidateId}/data",1L))
				.andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testGetAllCandidatesFieldsAll() throws Exception{
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/candidates/fields/all"))
				.andExpect(status().isOk()).andReturn();
	}
	
	@Test
	void testGetCandidateByIdDataAll()throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/candidates/{candidateId}/data/all",1))
				.andExpect(status().isOk()).andReturn();
	}

}
