/**
 * Copyright (c) 2014 InMotion Innovation Technology. All Rights Reserved. <BR>
 * <BR>
 * This software contains confidential and proprietary information of
 * InMotion Innovation Technology. ("Confidential Information").<BR>
 * <BR>
 * Such Confidential Information shall not be disclosed and it shall
 * only be used in accordance with the terms of the license agreement
 * entered into with IMI; other than in accordance with the written
 * permission of IMI. <BR>
 * 
 **/

package com.imi.dolphin.sdkwebservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DatabindContext;
import com.google.gson.Gson;
import com.imi.dolphin.sdkwebservice.builder.ButtonBuilder;
import com.imi.dolphin.sdkwebservice.builder.CarouselBuilder;
import com.imi.dolphin.sdkwebservice.builder.FormBuilder;
import com.imi.dolphin.sdkwebservice.builder.ImageBuilder;
import com.imi.dolphin.sdkwebservice.builder.QuickReplyBuilder;
import com.imi.dolphin.sdkwebservice.form.Datum;
import com.imi.dolphin.sdkwebservice.form.Formcuti;
import com.imi.dolphin.sdkwebservice.model.ButtonTemplate;
import com.imi.dolphin.sdkwebservice.model.EasyMap;
import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.model.MailModel;
import com.imi.dolphin.sdkwebservice.param.ParamSdk;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.token.Token;
import com.imi.dolphin.sdkwebservice.util.OkHttpUtil;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * @author reja
 *
 */
@Service
public class ServiceImp implements IService {
	public static final String OUTPUT = "output";
	private static final String SAMPLE_IMAGE_PATH = "https://goo.gl/SHdL8D";
	private static final String Image_cuti = "https://image.ibb.co/eAshTV/bot.jpg";
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	@Autowired
	AppProperties appProperties;

	@Autowired
	IMailService svcMailService;

	/**
	 * Get parameter value from request body parameter
	 * 
	 * @param extensionRequest
	 * @param name
	 * @return
	 */
	private String getEasyMapValueByName(ExtensionRequest extensionRequest, String name) {
		EasyMap easyMap = extensionRequest.getParameters().stream().filter(x -> x.getName().equals(name)).findAny()
				.orElse(null);
		if (easyMap != null) {
			return easyMap.getValue();
		}
		return "";
	}

	/*
	 * Sample Srn status with static result
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getSrnResult(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getSrnResult(ExtensionRequest extensionRequest) {
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		Map<String, String> output = new HashMap<>();
		StringBuilder respBuilder = new StringBuilder();
		respBuilder.append(
				"20-July-2018 16:10:32 Ahmad Mahatir Ridwan - PIC sudah onsite cek problem(printer nyala-mati)\n");
		respBuilder.append("PIC troubleshoot. restart printer(NOK), ganti kabel power(NOK)\n");
		respBuilder.append("PIC akan eskalasi ke vendor terkait.");
		output.put(OUTPUT, respBuilder.toString());
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Sample Customer Info with static result
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.imi.dolphin.sdkwebservice.service.IService#getCustomerInfo(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getCustomerInfo(ExtensionRequest extensionRequest) {
		String account = getEasyMapValueByName(extensionRequest, "account");
		String name = getEasyMapValueByName(extensionRequest, "name");
		Map<String, String> output = new HashMap<>();
		StringBuilder respBuilder = new StringBuilder();
		if (account.substring(0, 1).equals("1")) {
			respBuilder.append("Ticket Number : " + extensionRequest.getIntent().getTicket().getTicketNumber() + "\n");
			respBuilder.append(" Data Customer Account " + account + "\n");
			respBuilder.append("Nama: " + name + "\n");
			respBuilder.append("Setoran tiap bulan : Rp. 500,000\n");
			respBuilder.append("Jatuh tempo berikutnya : 15 Agustus 2018");
		} else {
			respBuilder.append("Ticket Number : " + extensionRequest.getIntent().getTicket().getTicketNumber() + "\n");
			respBuilder.append(appProperties.getFormId() + " Data Customer Account " + account + "\n");
			respBuilder.append("Nama: " + name + "\n");
			respBuilder.append("Setoran tiap bulan : Rp. 1,000,000\n");
			respBuilder.append("Jatuh tempo berikutnya : 27 Agustus 2018");
		}
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);

		output.put(OUTPUT, respBuilder.toString());
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Modify Customer Name Entity
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#clearCustomerName(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult modifyCustomerName(ExtensionRequest extensionRequest) {
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);

		Map<String, String> clearEntities = new HashMap<>();
		String name = getEasyMapValueByName(extensionRequest, "name");
		if (name.equalsIgnoreCase("reja")) {
			clearEntities.put("name", "budi");
			extensionResult.setEntities(clearEntities);
		}
		return extensionResult;
	}

	/*
	 * Sample Product info with static value
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getProductInfo(com.imi.dolphin
	 * .sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getProductInfo(ExtensionRequest extensionRequest) {
		String model = getEasyMapValueByName(extensionRequest, "model");
		String type = getEasyMapValueByName(extensionRequest, "type");

		Map<String, String> output = new HashMap<>();
		StringBuilder respBuilder = new StringBuilder();

		respBuilder.append("Untuk harga mobil " + model + " tipe " + type + " adalah 800,000,000\n");
		respBuilder.append("Jika kak {customer_name} tertarik, bisa klik tombol dibawah ini. \n");
		respBuilder.append("Maka nanti live agent kami akan menghubungi kakak ;)");

		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);

		output.put(OUTPUT, respBuilder.toString());
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Get messages from third party service
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getMessageBody(com.imi.dolphin
	 * .sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getMessageBody(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();
		StringBuilder respBuilder = new StringBuilder();

		try {
			OkHttpUtil okHttpUtil = new OkHttpUtil();
			okHttpUtil.init(true);
			Request request = new Request.Builder().url("https://jsonplaceholder.typicode.com/comments").get().build();
			Response response = okHttpUtil.getClient().newCall(request).execute();

			JSONArray jsonArray = new JSONArray(response.body().string());

			JSONObject jsonObject = jsonArray.getJSONObject(0);
			String message = jsonObject.getString("body");
			respBuilder.append(message);
		} catch (Exception e) {

		}

		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);

		output.put(OUTPUT, respBuilder.toString());
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Generate quick replies output
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IFormService#getQuickReplies(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getQuickReplies(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();
		QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Hello").add("Hello World", "hello world")
				.add("Hello Java", "B0F63CE1-F16F-4761-8881-F44C95D2792F").build();
		output.put(OUTPUT, quickReplyBuilder.string());
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Generate Forms
	 *
	 * (non-Javadoc)
	 * 
	 * @see com.imi.dolphin.sdkwebservice.service.IService#getForms(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getForms(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();
		FormBuilder formBuilder = new FormBuilder("FORM ID");

		ButtonTemplate button = new ButtonTemplate();
		button.setTitle("Title is here");
		button.setSubTitle("Subtitle is here");
		button.setPictureLink("Picture link");
		button.setPicturePath("Picture path");
		List<EasyMap> actions = new ArrayList<>();
		EasyMap bookAction = new EasyMap();
		bookAction.setName("Label here");
		bookAction.setValue(formBuilder.build());
		actions.add(bookAction);
		button.setButtonValues(actions);
		ButtonBuilder buttonBuilder = new ButtonBuilder(button);

		output.put(OUTPUT, buttonBuilder.build());
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Generate buttons output
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getButtons(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getButtons(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();

		ButtonTemplate button = new ButtonTemplate();
		button.setTitle("This is title");
		button.setSubTitle("This is subtitle");
		button.setPictureLink(SAMPLE_IMAGE_PATH);
		button.setPicturePath(SAMPLE_IMAGE_PATH);
		List<EasyMap> actions = new ArrayList<>();
		EasyMap bookAction = new EasyMap();
		bookAction.setName("Label");
		bookAction.setValue("Payload");
		actions.add(bookAction);
		button.setButtonValues(actions);

		ButtonBuilder buttonBuilder = new ButtonBuilder(button);
		output.put(OUTPUT, buttonBuilder.build());

		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Generate Carousel
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getCarousel(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getCarousel(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();

		ButtonTemplate button = new ButtonTemplate();
		button.setPictureLink(SAMPLE_IMAGE_PATH);
		button.setPicturePath(SAMPLE_IMAGE_PATH);
		button.setTitle("This is title");
		button.setSubTitle("This is subtitle");
		List<EasyMap> actions = new ArrayList<>();
		EasyMap bookAction = new EasyMap();
		bookAction.setName("Label");
		bookAction.setValue("Payload");
		actions.add(bookAction);
		button.setButtonValues(actions);
		ButtonBuilder buttonBuilder = new ButtonBuilder(button);

		ButtonTemplate button2 = new ButtonTemplate();
		button2.setPictureLink(SAMPLE_IMAGE_PATH);
		button2.setPicturePath(SAMPLE_IMAGE_PATH);
		button2.setTitle("This is title 2");
		button2.setSubTitle("This is subtitle 2");
		List<EasyMap> actions2 = new ArrayList<>();
		EasyMap bookAction2 = new EasyMap();
		bookAction2.setName("Label 2");
		bookAction2.setValue("Payload 2");
		actions2.add(bookAction2);
		button2.setButtonValues(actions2);
		ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);

		ButtonTemplate button3 = new ButtonTemplate();
		button3.setPictureLink(SAMPLE_IMAGE_PATH);
		button3.setPicturePath(SAMPLE_IMAGE_PATH);
		button3.setTitle("This is title 3");
		button3.setSubTitle("This is subtitle 3");
		button3.setButtonValues(actions2);
		ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);

		ButtonTemplate button4 = new ButtonTemplate();
		button4.setPictureLink(SAMPLE_IMAGE_PATH);
		button4.setPicturePath(SAMPLE_IMAGE_PATH);
		button4.setTitle("This is title 4");
		button4.setSubTitle("This is subtitle 4");
		button4.setButtonValues(actions2);
		ButtonBuilder buttonBuilder4 = new ButtonBuilder(button4);

		ButtonTemplate button5 = new ButtonTemplate();
		button5.setPictureLink(SAMPLE_IMAGE_PATH);
		button5.setPicturePath(SAMPLE_IMAGE_PATH);
		button5.setTitle("This is title 5");
		button5.setSubTitle("This is subtitle 5");
		button5.setButtonValues(actions2);
		ButtonBuilder buttonBuilder5 = new ButtonBuilder(button5);

		CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder.build(), buttonBuilder2.build(),
				buttonBuilder3.build(), buttonBuilder4.build(), buttonBuilder5.build());

		output.put(OUTPUT, carouselBuilder.build());

		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Transfer ticket to agent
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#doTransferToAgent(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult doTransferToAgent(ExtensionRequest extensionRequest) {
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(true);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(false);
		return extensionResult;
	}

	/*
	 * Send Location
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#doSendLocation(com.imi.dolphin
	 * .sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult doSendLocation(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();
		QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Kirim lokasi kakak ya")
				.add("location", "location").build();
		output.put(OUTPUT, quickReplyBuilder.string());
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Generate Image
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.imi.dolphin.sdkwebservice.service.IService#getImage(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getImage(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();

		ButtonTemplate image = new ButtonTemplate();
		image.setPictureLink(SAMPLE_IMAGE_PATH);
		image.setPicturePath(SAMPLE_IMAGE_PATH);

		ImageBuilder imageBuilder = new ImageBuilder(image);
		output.put(OUTPUT, imageBuilder.build());

		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Split bubble chat conversation
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getSplitConversation(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult getSplitConversation(ExtensionRequest extensionRequest) {
		String firstLine = "Terima kasih {customer_name}";
		String secondLine = "Data telah kami terima dan agent kami akan proses terlebih dahulu ya kak";
		Map<String, String> output = new HashMap<>();
		output.put(OUTPUT, firstLine + ParamSdk.SPLIT_CHAT + secondLine);

		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	/*
	 * Send mail configuration on application.properties file
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#doSendMail(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
	 */
	@Override
	public ExtensionResult doSendMail(ExtensionRequest extensionRequest) {
		String recipient = getEasyMapValueByName(extensionRequest, "recipient");
		MailModel mailModel = new MailModel(recipient, "3Dolphins SDK Mail Subject", "3Dolphins SDK mail content");
		String sendMailResult = svcMailService.sendMail(mailModel);

		Map<String, String> output = new HashMap<>();
		output.put(OUTPUT, sendMailResult);
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	public Datum getForm(String bearer, String ticketNumber) {
		Datum data = new Datum();
		String baseUrl = appProperties.getBaseUrl();
		String apiform = appProperties.getApiForm();
		String formId = appProperties.getFormIdCuti();
		String paramformId = "?formId=";
		String paramFieldName = "&fieldName=";
		String paramFieldValue = "&fieldValue=*";
		String paramStart = "&start=0";
		String paramCount = "&count=1";
		String fieldName = appProperties.getTicketNumber();
		
		//menggunakan ticket number
		String url = baseUrl + apiform + paramformId + formId + paramFieldName + fieldName + paramFieldValue+ ticketNumber + paramStart + paramCount;

		// tidak pakai ticket number
//		String url = baseUrl + apiform + paramformId + formId + paramFieldName + fieldName + paramFieldValue
//				+ paramStart + paramCount;
		System.out.println(url);

		OkHttpUtil okHttpUtil = new OkHttpUtil();
		okHttpUtil.init(true);

		Request request = new Request.Builder().url(url).get().addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + bearer).build();

		try {
			Response response = okHttpUtil.getClient().newCall(request).execute();

			Formcuti fct = new Formcuti();
			String JsonString = response.body().string();
			// System.out.println("getform, Jsonstring : "+JsonString);

			Gson gson = new Gson();
			fct = gson.fromJson(JsonString, Formcuti.class);
			// System.out.println("From cuti data : "+fct);

			String jsonfct = gson.toJson(fct.getData());
			// System.out.println(jsonfct);

			// ambil json array
			JSONArray arrayJson = new JSONArray(jsonfct);
			// System.out.println("Array json dari form cuti data : "+arrayJson);
			JSONObject jsonObjek = arrayJson.getJSONObject(0);
			// System.out.println("Object json dari form cuti data : "+jsonObjek);

			data = gson.fromJson(jsonObjek.toString(), Datum.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}

	public String getToken() {
		String bearer = "";
		String username = "michael.samuel@inmotion.co.id";
		String password = "Michael@123";
		String baseUrl = appProperties.getBaseUrl();
		String apiToken = appProperties.getApiToken();

		String url = baseUrl + apiToken;

		Token tk = new Token();
		tk.setUsername(username);
		tk.setPassword(password);

		Gson gson = new Gson();

		OkHttpUtil okHttpUtil = new OkHttpUtil();
		okHttpUtil.init(true);

		RequestBody body = RequestBody.create(JSON, gson.toJson(tk));
		// System.out.println(gson.toJson(tk).toString());

		Request request = new Request.Builder().url(url).addHeader("Content-Type", "application/json; charset=utf-8")
				.post(body).build();

		try {
			Response response = okHttpUtil.getClient().newCall(request).execute();

			JSONObject jsonObjek = new JSONObject(response.body().string());
			bearer = jsonObjek.getString("token");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bearer;
	}

	@Override
	public ExtensionResult dogetFormcuti(ExtensionRequest extensionRequest) {

 		Map<String, String> output = new HashMap<>();
		String formId = appProperties.getFormIdCuti();
		FormBuilder formBuilder = new FormBuilder(formId);
		ButtonTemplate button = new ButtonTemplate();
		button.setPictureLink(Image_cuti);
		button.setPicturePath(Image_cuti);
		button.setTitle("This is title");
		button.setSubTitle("This is subtitle");
		button.setTitle("Form Cuti");
		button.setSubTitle("Form Cuti");
		List<EasyMap> actions = new ArrayList<>();
		EasyMap bookAction = new EasyMap();
		bookAction.setName("Isi Form");
		bookAction.setValue(formBuilder.build());
		actions.add(bookAction);
		button.setButtonValues(actions);
		ButtonBuilder buttonBuilder = new ButtonBuilder(button);

		output.put(OUTPUT, buttonBuilder.build());
		ExtensionResult extensionResult = new ExtensionResult();
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}

	@Override
	public ExtensionResult dogetajuincuti(ExtensionRequest extensionRequest) {
		Map<String, String> output = new HashMap<>();
		ExtensionResult extensionResult = new ExtensionResult();
		StringBuilder respBuilder = new StringBuilder();
		StringBuilder respBuilder2 = new StringBuilder();

		String ticketNumber = extensionRequest.getIntent().getTicket().getTicketNumber();

		String Bearer = "";
		String nama = "";
		String nik = "";
		String lembaga = "";
		String ijinuntuk = "";
		String permohonanijin = "";
		String tanggal = "";
		String waktuijin = "";
		String keperluan = "";

		// 1.get data dari form
		// ambil token

		Bearer = getToken();
		// request API dolphin
		System.out.println("bearer = " + Bearer);

		Datum data = new Datum();
		data = getForm(Bearer, ticketNumber);
		// 2. parsing data

		nama = data.getNama();
		nik = data.getNik();
		lembaga = data.getLembaga();
		ijinuntuk = data.getMengajukanPermohonanIjinUntuk();
		permohonanijin = data.getPermohonanIjin();
		tanggal = data.getTanggal();
		waktuijin = data.getWaktuIjin();
		keperluan = data.getKeperluanKeterangan();
		String namaatasan = appProperties.getNamerecipient1();
		String namaHrd = appProperties.getNamerecipient2();

		respBuilder.append("Kepada Yth.\n");
		respBuilder.append(namaatasan);
		respBuilder.append("\nDengan Hormat,");
		respBuilder.append("\n\nNama : " + nama);
		respBuilder.append("\nNIK : " + nik);
		respBuilder.append("\nLembaga : " + lembaga);
		respBuilder.append("\nPermohonan ijin untuk  :" + ijinuntuk);
		respBuilder.append("\ntanggal : " + tanggal);
		respBuilder.append("\nwaktu ijin : " + waktuijin);
		respBuilder.append("\nkeperluan : " + keperluan);
		respBuilder.append("\n\nDemikian Surat cuti ini dibuat.");
		respBuilder.append("\nterima kasih");

		respBuilder2.append("Kepada Yth.\n");
		respBuilder2.append(namaHrd);
		respBuilder2.append("\nDengan Hormat,");
		respBuilder2.append("\n\nNama : " + nama);
		respBuilder2.append("\nNIK : " + nik);
		respBuilder2.append("\nLembaga : " + lembaga);
		respBuilder2.append("\nPermohonan ijin untuk  :" + ijinuntuk);
		respBuilder2.append("\ntanggal : " + tanggal);
		respBuilder2.append("\nwaktu ijin : " + waktuijin);
		respBuilder2.append("\nkeperluan : " + keperluan);
		respBuilder2.append("\n\nDemikian Surat cuti ini dibuat.");
		respBuilder2.append("\nterima kasih");

		String bodyAtasan = respBuilder.toString();
		String bodyHrd = respBuilder2.toString();

		// 3. kirim email
		String recipient1 = getEasyMapValueByName(extensionRequest, "email1");
		String recipient2 = getEasyMapValueByName(extensionRequest, "email2");
		System.out.println("recipient 1 : " + recipient1);
		System.out.println("recipient 2 : " + recipient2);

		// String recipient1 = appProperties.getEmailrecipient1();
		// String recipient2 = appProperties.getEmailrecipient2();
		MailModel mailModel = new MailModel(recipient1, "Surat Ijin Cuti", bodyAtasan);
		MailModel mailModel2 = new MailModel(recipient2, "Surat Ijin Cuti", bodyHrd);
		String sendMailResult = svcMailService.sendMail(mailModel);
		String sendMailResult2 = svcMailService.sendMail(mailModel2);
		System.out.println("hasil kirim email" + sendMailResult);
		System.out.println("hasil kirim email" + sendMailResult2);

		String result = "";
		if (sendMailResult.toLowerCase().contains("success") && sendMailResult2.toLowerCase().contains("success")) {
			result = "Baik kak silahkan tunggu konfirmasi ya kak";
		} else if (sendMailResult.toLowerCase().contains("fail") && sendMailResult2.toLowerCase().contains("fail")) {
			result = "Maaf kak pengiriman email gagal. Boleh diulangi kak";
		} else {
			result = "Maaf kak {bot_name} belum mengerti. Bisa tolong ulangi lagi kak.";
		}

		output.put(OUTPUT, result);
		extensionResult.setAgent(false);
		extensionResult.setRepeat(false);
		extensionResult.setSuccess(true);
		extensionResult.setNext(true);
		extensionResult.setValue(output);
		return extensionResult;
	}
    @Override
    public ExtensionResult doGetCuaca(String kota, ExtensionRequest extensionRequest) {
        String uri = "https://api.openweathermap.org/data/2.5/weather?q=" + kota + "&appid=beb536b6a3f98bb2bfde28ac6d99c6fc";
        URL url;
        String inline = "";
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        try {
            url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode == 200) {
                Scanner sc = new Scanner(url.openStream());
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                sc.close();
            }
            JSONObject obj = new JSONObject(inline);
            JSONObject main = obj.getJSONObject("main");
            int temp = main.getInt("temp");
            inline = temp + "";
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.put("cuaca", inline);
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }
}
