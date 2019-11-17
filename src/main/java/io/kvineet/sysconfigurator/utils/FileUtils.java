package io.kvineet.sysconfigurator.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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

	public static <T> boolean saveToFile(T[] contents, String fileName) {

		Path path = Paths.get(fileName);
		if (true) {
			try {
				createFileIfNotExists(fileName);
				byte[] byteContents = JsonUtils.toJsonBytes(contents);
				Files.write(path, byteContents);
				return true;
			} catch (Exception e) {
				System.out.println(String.format("Unable to save content in file: {}, \nError: {}", fileName, e));
			}
		}
		return false;
	}
}
