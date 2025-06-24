/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.extension.spring.request;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Mock implementation of the {@link MultipartFile}
 * interface.
 *
 * @author Juergen Hoeller
 * @author Eric Crampton
 * @since 2.0
 */
public class MockMultipartFile implements MultipartFile {

	private final String name;

	private final String originalFilename;

	@Nullable
	private final String contentType;

	private final byte[] content;


	/**
	 * Create a new MockMultipartFile with the given content.
	 * @param name the name of the file
	 * @param content the content of the file
	 */
	public MockMultipartFile(String name, @Nullable byte[] content) {
		this(name, "", null, content);
	}

	/**
	 * Create a new MockMultipartFile with the given content.
	 * @param name the name of the file
	 * @param contentStream the content of the file as stream
	 * @throws IOException if reading from the stream failed
	 */
	public MockMultipartFile(String name, InputStream contentStream) throws IOException {
		this(name, "", null, FileCopyUtils.copyToByteArray(contentStream));
	}

	/**
	 * Create a new MockMultipartFile with the given content.
	 * @param name the name of the file
	 * @param originalFilename the original filename (as on the client's machine)
	 * @param contentType the content type (if known)
	 * @param content the content of the file
	 */
	public MockMultipartFile(
			String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content) {

		Assert.hasLength(name, "Name must not be empty");
		this.name = name;
		this.originalFilename = (originalFilename != null ? originalFilename : "");
		this.contentType = contentType;
		this.content = (content != null ? content : new byte[0]);
	}

	/**
	 * Create a new MockMultipartFile with the given content.
	 * @param name the name of the file
	 * @param originalFilename the original filename (as on the client's machine)
	 * @param contentType the content type (if known)
	 * @param contentStream the content of the file as stream
	 * @throws IOException if reading from the stream failed
	 */
	public MockMultipartFile(
			String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
			throws IOException {

		this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
	}


	@Override
	public String getName() {
		return this.name;
	}

	@Override
	@NonNull
	public String getOriginalFilename() {
		return this.originalFilename;
	}

	@Override
	@Nullable
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public boolean isEmpty() {
		return (this.content.length == 0);
	}

	@Override
	public long getSize() {
		return this.content.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return this.content;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.content);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		FileCopyUtils.copy(this.content, dest);
	}

}
