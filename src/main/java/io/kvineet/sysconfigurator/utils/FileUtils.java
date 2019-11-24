package io.kvineet.sysconfigurator.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

public final class FileUtils {

	public static <T> Optional<T> getFromFile(String fileName, boolean createIfNotExist, Class<T> clazz) {

		Path path = Paths.get(fileName);
		try {
			createFileIfNotExists(fileName);
			byte[] fileData = Files.readAllBytes(path);
			return Optional.ofNullable(JsonUtils.fromJson(fileData, clazz));
		} catch (Exception e) {
			System.out.println(String.format("Unable to read file: {}, \nError: {}", fileName, e));
		}
		return Optional.empty();
	}

	private static void createFileIfNotExists(String fileName) {

		Path path = Paths.get(fileName);
		File file = path.toFile();
		if (!file.exists()) {
			try {
				Files.createFile(path);
			} catch (IOException e1) {
				System.out.println(String.format("Unable to create file at location: {}, \nError: {}", fileName, e1));
				e1.printStackTrace();
			}
		}
	}

	public static <T> boolean save(T[] contents, String fileName)
			throws AccessDeniedException, JsonProcessingException {

		byte[] byteContents = {};
		try {
			byteContents = JsonUtils.toJsonBytes(contents);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw e;
		}
		return save(byteContents, fileName);
	}

	public static boolean save(String fileContents, String fileName) throws AccessDeniedException {
		byte[] byteContents = fileContents.getBytes();
		return save(byteContents, fileName);
	}

	private static boolean save(byte[] byteContents, String fileName) throws AccessDeniedException {

		Path path = Paths.get(fileName);
		try {
			createFileIfNotExists(fileName);
			Files.write(path, byteContents);
			return true;
		} catch (Exception e) {
			throw new AccessDeniedException(fileName);
		}
	}
}
