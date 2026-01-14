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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    /**
     * 普通长度字符测试
     */
    @GetMapping("/normalComment")
    public void normalComment() {

    }

    /**
     * “昨夜闲潭梦落花”“我欲因之梦吴越”“铁马冰河入梦来“梦往往以一种独特的方式呈现我们的感受和期冀，为我们打开更浩瀚的天空。我们也常常向别人讲述自己的梦，用文字记录自己的梦，以行动实现自己的梦
     */
    @GetMapping("/longComment")
    public void longComment() {

    }
}
