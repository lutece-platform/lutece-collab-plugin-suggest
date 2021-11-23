/*
 * Copyright (c) 2002-2020, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.suggest.web;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.CategoryHome;
import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.DefaultMessage;
import fr.paris.lutece.plugins.suggest.business.DefaultMessageHome;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestAction;
import fr.paris.lutece.plugins.suggest.business.SuggestActionHome;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitState;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitType;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitTypeHome;
import fr.paris.lutece.plugins.suggest.business.EntryAdditionalAttribute;
import fr.paris.lutece.plugins.suggest.business.EntryAdditionalAttributeHome;
import fr.paris.lutece.plugins.suggest.business.EntryFilter;
import fr.paris.lutece.plugins.suggest.business.EntryHome;
import fr.paris.lutece.plugins.suggest.business.EntryType;
import fr.paris.lutece.plugins.suggest.business.EntryTypeHome;
import fr.paris.lutece.plugins.suggest.business.ExportFormat;
import fr.paris.lutece.plugins.suggest.business.FormError;
import fr.paris.lutece.plugins.suggest.business.IEntry;
import fr.paris.lutece.plugins.suggest.business.ReportedMessageHome;
import fr.paris.lutece.plugins.suggest.business.Response;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.VoteType;
import fr.paris.lutece.plugins.suggest.business.VoteTypeHome;
import fr.paris.lutece.plugins.suggest.service.CategoryResourceIdService;
import fr.paris.lutece.plugins.suggest.service.CommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.DefaultMessageResourceIdService;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestResourceIdService;
import fr.paris.lutece.plugins.suggest.service.ExportFormatResourceIdService;
import fr.paris.lutece.plugins.suggest.service.ICommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.suggestsearch.SuggestSearchService;
import fr.paris.lutece.plugins.suggest.service.search.SuggestIndexer;
import fr.paris.lutece.plugins.suggest.utils.SuggestIndexerUtils;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.plugins.suggest.web.action.SuggestAdminSearchFields;
import fr.paris.lutece.plugins.suggest.web.action.ISuggestAction;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.ThemesService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.PluginActionManager;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage form features ( manage, create, modify, remove)
 */
public class SuggestJspBean extends PluginAdminPageJspBean
{
    public static final String MARK_DISABLE_SUGGEST_SUBMIT_STATE_NUMBER = "disable_state_number";
    public static final String MARK_WAITING_FOR_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER = "waiting_for_publish_state_number";
    public static final String MARK_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER = "publish_state_number";
    private static final long serialVersionUID = 8034293907026887250L;

    // templates
    private static final String TEMPLATE_MANAGE_SUGGEST = "admin/plugins/suggest/manage_suggest.html";
    private static final String TEMPLATE_MANAGE_SUGGEST_SUBMIT = "admin/plugins/suggest/manage_suggest_submit.html";
    private static final String TEMPLATE_CREATE_SUGGEST_SUBMIT = "admin/plugins/suggest/create_suggest_submit.html";
    private static final String TEMPLATE_MANAGE_SUGGEST_SUBMIT_ORDER = "admin/plugins/suggest/manage_suggest_submit_order.html";
    private static final String TEMPLATE_MANAGE_COMMENT_SUBMIT = "admin/plugins/suggest/manage_comment_submit.html";
    private static final String TEMPLATE_CREATE_SUGGEST = "admin/plugins/suggest/create_suggest.html";
    private static final String TEMPLATE_MODIFY_SUGGEST = "admin/plugins/suggest/modify_suggest.html";
    private static final String TEMPLATE_STATISTICS_SUGGEST = "admin/plugins/suggest/statistics.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/suggest/manage_advanced_parameters.html";

    // message
    private static final String MESSAGE_NEW_COMMENT_SUBMIT_INVALID = "suggest.message.newCommentSubmitInvalid";
    private static final String MESSAGE_CONFIRM_REMOVE_SUGGEST = "suggest.message.confirmRemoveSuggest";
    private static final String MESSAGE_CONFIRM_REMOVE_SUGGEST_SUBMIT = "suggest.message.confirmRemoveSuggestSubmit";
    private static final String MESSAGE_CONFIRM_REMOVE_COMMENT_SUBMIT = "suggest.message.confirmRemoveCommentSubmit";
    private static final String MESSAGE_CONFIRM_REMOVE_SUGGEST_WITH_SUGGEST_SUBMIT = "suggest.message.confirmRemoveSuggestWithSuggestSubmit";
    private static final String MESSAGE_CONFIRM_DISABLE_SUGGEST = "suggest.message.confirmDisableSuggest";
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY = "suggest.message.confirmRemoveEntry";
    private static final String MESSAGE_CONFIRM_UPDATE_ALL_SUGGEST_SUBMIT = "suggest.message.confirmUpdateAllSuggestSubmit";
    private static final String MESSAGE_MANDATORY_FIELD = "suggest.message.mandatory.field";
    private static final String MESSAGE_ILLOGICAL_NUMBER_VOTE_REQUIRED = "suggest.message.illogicalNumberVoteRequired";
    private static final String MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN = "suggest.message.illogicalNumberSuggestSubmitCaractersShown";
    private static final String MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE = "suggest.message.illogicalNumberSuggestSumitInTopScore";
    private static final String MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT = "suggest.message.illogicalNumberSuggestSumitInTopComment";
    private static final String MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_PER_PAGE = "suggest.message.illogicalNumberSuggestSumitPerPage";
    private static final String MESSAGE_ILLOGICAL_NUMBER_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST = "suggest.message.illogicalNumberCommentDisplayInSuggestSubmitList";
    private static final String MESSAGE_ILLOGICAL_NUMBER_CHAR_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST = "suggest.message.illogicalNumberCharCommentDisplayInSuggestSubmitList";
    private static final String MESSAGE_ILLOGICAL_NUMBER_DAY_REQUIRED = "suggest.message.illogicalNumberDayRequired";
    private static final String MESSAGE_CONFIRM_CHANGE_SUGGEST_SUBMIT_CATEGORY = "suggest.message.confirmChangeSuggestSubmitCategory";
    private static final String MESSAGE_CONFIRM_REMOVE_SUGGEST_SUBMIT_CATEGORY = "suggest.message.confirmRemoveSuggestSubmitCategory";
    private static final String MESSAGE_ERROR_NO_CATEGORY = "suggest.message.errorNoCategorySelected";
    private static final String MESSAGE_ERROR_NO_SUGGEST_SUBMIT_TYPE_SELECTED = "suggest.message.errorNoSuggestSubmitTypeSelected";
    private static final String MESSAGE_MANDATORY_QUESTION = "suggest.message.mandatory.question";
    private static final String MESSAGE_FORM_ERROR = "suggest.message.formError";
    private static final String FIELD_TITLE = "suggest.createSuggest.labelTitle";
    private static final String FIELD_LIBELLE_CONTRIBUTION = "suggest.createSuggest.labelLibelleContribution";
    private static final String FIELD_UNAVAILABILITY_MESSAGE = "suggest.createSuggest.labelUnavailabilityMessage";
    private static final String FIELD_VOTE_TYPE = "suggest.createSuggest.labelVoteType";
    private static final String FIELD_LIBELE_VALIDATE_BUTTON = "suggest.createSuggest.labelLibelleValidateButton";
    private static final String FIELD_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN = "suggest.createSuggest.labelNumberSuggestSubmitCaractersShown";
    private static final String FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE = "suggest.createSuggest.labelNumberSuggestSubmitInTopScore";
    private static final String FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT = "suggest.createSuggest.labelNumberSuggestSubmitInTopComment";
    private static final String FIELD_NUMBER_SUGGEST_SUBMIT_PER_PAGE = "suggest.createSuggest.labelNumberSuggestSubmitPerPage";
    private static final String FIELD_NUMBER_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST = "suggest.createSuggest.labelNumberCommentDisplayInSuggestSubmitList";
    private static final String FIELD_NUMBER_CHAR_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST = "suggest.createSuggest.labelNumberCharCommentDisplayInSuggestSubmitList";

    private static final String FIELD_NOTIFICATION_NEW_COMMENT_TITLE = "suggest.createSuggest.labelNotificationNewCommentTitle";
    private static final String FIELD_NOTIFICATION_NEW_COMMENT_BODY = "suggest.createSuggest.labelNotificationNewCommentBody";
    private static final String FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_TITLE = "suggest.createSuggest.labelNotificationNewSuggestSubmitTitle";
    private static final String FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_BODY = "suggest.createSuggest.labelNotificationNewSuggestSubmitBody";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "suggest.itemsPerPage";
    private static final String PROPERTY_ALL = "suggest.manageSuggest.select.all";
    private static final String PROPERTY_YES = "suggest.manageSuggestSubmit.select.yes";
    private static final String PROPERTY_NO = "suggest.manageSuggestSubmit.select.no";
    private static final String PROPERTY_ENABLE = "suggest.manageSuggest.select.enable";
    private static final String PROPERTY_DISABLE = "suggest.manageSuggest.select.disable";
    private static final String PROPERTY_NOTHING = "suggest.createSuggest.select.nothing";
    private static final String PROPERTY_MODIFY_SUGGEST_TITLE = "suggest.modifySuggest.title";
    private static final String PROPERTY_COPY_SUGGEST_TITLE = "suggest.copySuggest.title";
    private static final String PROPERTY_COPY_ENTRY_TITLE = "suggest.copyEntry.title";
    private static final String PROPERTY_CREATE_SUGGEST_TITLE = "suggest.createSuggest.title";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = ".createEntry.titleQuestion";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "suggest.modifyEntry.titleQuestion";
    private static final String PROPERTY_NUMBER_SUGGEST_SUBMIT_VALUE_SHOWN_CHARACTERS = "suggest.suggestSubmitValue.NumberShownCharacters";
    private static final String PROPERTY_SUGGESTSUBMIT_HIGHSCORES = "suggest.highscores.suggestSubmit.number";
    private static final String PROPERTY_MANAGE_SUGGEST_PAGE_TITLE = "suggest.manageSuggest.pageTitle";
    private static final String PROPERTY_MANAGE_ADVANCED_PARAMETERS_PAGE_TITLE = "suggest.manageAdvancedParameters.pageTitle";
    private static final String PROPERTY_MANAGE_SUGGEST_SUBMIT_PAGE_TITLE = "suggest.manageSuggestSubmit.pageTitle";
    private static final String PROPERTY_MANAGE_SUGGEST_SUBMIT_ORDER_PAGE_TITLE = "suggest.manageSuggestSubmitOrder.pageTitle";
    private static final String PROPERTY_CREATE_SUGGEST_SUBMIT_PAGE_TITLE = "suggest.createSuggestSubmit.pageTitle";
    private static final String PROPERTY_MANAGE_COMMENT_SUBMIT_PAGE_TITLE = "suggest.manageCommentSubmit.pageTitle";
    private static final String PROPERTY_DEFAULT_ROLE_CODE = "defaultRole.code";
    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_USER_WORKGROUP_SELECTED = "user_workgroup_selected";
    private static final String MARK_SUGGEST_STATE_REF_LIST = "suggest_state_list";
    private static final String MARK_SUGGEST_STATE_SELECTED = "suggest_state_selected";
    private static final String MARK_SUGGEST_SUBMIT_STATE_REF_LIST = "suggest_submit_state_list";
    private static final String MARK_SUGGEST_SUBMIT_STATE_SELECTED = "suggest_submit_state_selected";
    private static final String MARK_MAILING_REF_LIST = "mailing_list";
    private static final String MARK_ENTRY_TYPE_REF_LIST = "entry_type_list";
    private static final String MARK_REGULAR_EXPRESSION_LIST_REF_LIST = "regular_expression_list";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_ID_ENTRY_FIRST_IN_THE_LIST = "id_entry_first_in_the_list";
    private static final String MARK_ID_ENTRY_LAST_IN_THE_LIST = "id_entry_last_in_the_list";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_SUGGEST_LIST = "suggest_list";
    private static final String MARK_SUGGEST_SUBMIT_LIST = "suggest_submit_list";
    private static final String MARK_COMMENT_SUBMIT_LIST = "comment_submit_list";
    private static final String MARK_VOTE_TYPE_LIST = "vote_type_list";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_SUGGEST_SUBMIT_TYPE_LIST = "suggest_submit_type_list";
    private static final String MARK_SUGGEST = "suggest";
    private static final String MARK_SUGGEST_SUBMIT = "suggest_submit";
    private static final String MARK_ID_SUGGEST_SUBMIT_PREV = "suggest_submit_prev";
    private static final String MARK_ID_SUGGEST_SUBMIT_NEXT = "suggest_submit_next";
    private static final String MARK_PERMISSION_CREATE_SUGGEST = "permission_create_suggest";
    private static final String MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS = "permission_manage_advanced_parameters";
    private static final String MARK_ENTRY_TYPE_GROUP = "entry_type_group";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_LIST = "list";
    private static final String MARK_NUMBER_QUESTION = "number_question";
    private static final String MARK_NUMBER_ITEMS = "number_items";
    private static final String MARK_DEFAULT_MESSAGE = "default_message";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_YES_VALUE = "yes_value";
    private static final String MARK_NO_VALUE = "no_value";
    private static final String MARK_NUMBER_SHOWN_CHARACTERS = "number_shown_characters";
    private static final String MARK_LIST_SUGGEST_SUBMIT_SORT = "suggest_submit_sort_list";
    private static final String MARK_SUGGEST_SUBMIT_SORT_SELECTED = "suggest_submit_sort_selected";
    private static final String MARK_SUGGEST_SUBMIT_CATEGORY_SELECTED = "suggest_submit_category_selected";
    private static final String MARK_SUGGEST_SUBMIT_TYPE_SELECTED = "suggest_submit_type_selected";
    private static final String MARK_CATEGORY_FILTER = "category_filter";
    private static final String MARK_TYPE_FILTER = "type_filter";
    private static final String MARK_REPORT_REF_LIST = "suggest_submit_report_list";
    private static final String MARK_REPORT_SELECTED = "suggest_submit_report_selected";
    private static final String MARK_CONTAINS_COMMENT_DISABLE_LIST = "suggest_submit_contains_comment_disable_list";
    private static final String MARK_CONTAINS_COMMENT_DISABLE_SELECTED = "suggest_submit_contains_comment_disable_selected";
    private static final String MARK_AUTHENTIFICATION_ENABLE = "authentification_enable";
    private static final String MARK_ROLE_LIST = "role_list";
    private static final String MARK_DEFAULT_VALUE_ROLE = "default_value_role";
    private static final String MARK_COMMENT_SORT_LIST = "comment_sort_list";
    private static final String MARK_COMMENT_SORT_SELECTED = "comment_sort_selected";
    private static final String MARK_COMMENT_STATE_LIST = "comment_state_list";
    private static final String MARK_COMMENT_STATE_SELECTED = "comment_state_selected";
    private static final String MARK_CONTAINS_SUB_COMMENT_DISABLE_LIST = "comment_contains_sub_comment_disable_list";
    private static final String MARK_CONTAINS_SUB_COMMENT_DISABLE_SELECTED = "comment_contains_sub_comment_disable_selected";
    private static final String MARK_SUGGEST_SUBMIT_ORDER_LIST = "order_list";
    private static final String MARK_SUGGEST_SUBMIT_ORDER_LIST_PINNED = "order_list_pinned";
    private static final String MARK_FIRST_DATE_FILTER = "first_date_filter";
    private static final String MARK_LAST_DATE_FILTER = "last_date_filter";
    private static final String MARK_NUMBER_VOTES = "number_votes";
    private static final String MARK_NUMBER_COMMENTS = "number_comments";
    private static final String MARK_NUMBER_SUGGESTSUBMIT_DISABLED = "number_suggestsubmit_disabled";
    private static final String MARK_NUMBER_SUGGESTSUBMIT_WAITING = "number_suggestsubmit_waiting";
    private static final String MARK_NUMBER_SUGGESTSUBMIT_PUBLISHED = "number_suggestsubmit_published";
    private static final String MARK_HIGH_SCORES = "high_scores";
    private static final String MARK_NUMBER_USERS = "number_users";
    private static final String MARK_URL = "url";
    private static final String MARK_DEFAULT_THEME = "default_theme";
    private static final String MARK_THEME_REF_LIST = "theme_list";
    private static final String MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE = "permission_manage_default_message";
    private static final String MARK_PERMISSION_MANAGE_CATEGORY = "permission_manage_category";
    private static final String MARK_PERMISSION_MANAGE_EXPORT_FORMAT = "permission_manage_export_format";
    private static final String MARK_ID_DEFAULT_SUGGEST = "id_default_suggest";
    private static final String MARK_QUERY = "query";
    private static final String MARK_SUGGEST_ACTIONS = "suggest_actions";
    private static final String MARK_ID_PARENT = "id_parent";
    private static final String MARK_PANEL = "panel";

    // Jsp Definition
    private static final String JSP_DO_DISABLE_SUGGEST = "jsp/admin/plugins/suggest/DoDisableSuggest.jsp";
    private static final String JSP_DO_REMOVE_SUGGEST = "jsp/admin/plugins/suggest/DoRemoveSuggest.jsp";
    private static final String JSP_DO_REMOVE_SUGGEST_SUBMIT = "jsp/admin/plugins/suggest/DoRemoveSuggestSubmit.jsp";
    private static final String JSP_DO_REMOVE_ENTRY = "jsp/admin/plugins/suggest/DoRemoveEntry.jsp";
    private static final String JSP_MANAGE_SUGGEST = "jsp/admin/plugins/suggest/ManageSuggest.jsp";
    private static final String JSP_DO_REMOVE_COMMENT_SUBMIT = "jsp/admin/plugins/suggest/DoRemoveCommentSubmit.jsp";
    // private static final String JSP_MANAGE_SUGGEST_SUBMIT_TYPE = "jsp/admin/plugins/suggest/ManageSuggestSubmitType.jsp";
    private static final String JSP_MODIFY_SUGGEST = "jsp/admin/plugins/suggest/ModifySuggest.jsp";
    private static final String JSP_MODIFY_ENTRY = "jsp/admin/plugins/suggest/ModifyEntry.jsp";
    private static final String JSP_MANAGE_SUGGEST_SUBMIT = "jsp/admin/plugins/suggest/ManageSuggestSubmit.jsp";
    private static final String JSP_MANAGE_SUGGEST_SUBMIT_ORDER = "jsp/admin/plugins/suggest/ManageSuggestSubmitOrder.jsp";
    private static final String JSP_MANAGE_COMMENT_SUBMIT = "jsp/admin/plugins/suggest/ManageCommentSubmit.jsp";
    private static final String JSP_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/suggest/ManageAdvancedParameters.jsp";
    private static final String JSP_DO_UPDATE_ALL_SUGGEST_SUBMIT = "jsp/admin/plugins/suggest/DoUpdateAllSuggestSubmit.jsp";
    private static final String JSP_DO_CHANGE_SUGGEST_SUBMIT_CATEGORY = "jsp/admin/plugins/suggest/DoMassChangeSuggestSubmitCategory.jsp";

    // parameters form
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";
    private static final String PARAMETER_PANEL = "panel";

    private static final String PARAMETER_ID_SUGGEST_SUBMIT = "id_suggest_submit";
    private static final String PARAMETER_ID_COMMENT_SUBMIT = "id_comment_submit";
    private static final String PARAMETER_ID_PARENT = "id_parent";
    private static final String PARAMETER_STATE_NUMBER = "state_number";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_LIBELLE_CONTRIBUTION = "libelle_contribution";
    private static final String PARAMETER_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_ID_VOTE_TYPE = "id_vote_type";
    private static final String PARAMETER_ACTIVE_PROPOSITION_STATE = "active_suggest_proposition_state";
    private static final String PARAMETER_NUMBER_VOTE_REQUIRED = "number_vote_required";
    private static final String PARAMETER_NUMBER_DAY_REQUIRED = "number_day_required";
    private static final String PARAMETER_ACTIVE_SUGGEST_SUBMIT_AUTHENTIFICATION = "active_suggest_submit_authentification";
    private static final String PARAMETER_ACTIVE_VOTE_AUTHENTIFICATION = "active_vote_authentification";
    private static final String PARAMETER_ACTIVE_COMMENT_AUTHENTIFICATION = "active_comment_authentification";
    private static final String PARAMETER_DISABLE_NEW_SUGGEST_SUBMIT = "disable_new_suggest_submit";
    private static final String PARAMETER_ENABLE_MAIL_NEW_SUGGEST_SUBMIT = "enable_mail_new_suggest_submit";
    private static final String PARAMETER_ENABLE_MAIL_NEW_COMMENT_SUBMIT = "enable_mail_new_comment_submit";
    private static final String PARAMETER_ENABLE_MAIL_NEW_REPORTED_SUBMIT = "enable_mail_new_reported_submit";
    private static final String PARAMETER_ID_MAILING_LIST_SUGGEST_SUBMIT = "id_mailing_list_suggest_submit";
    private static final String PARAMETER_AUTHORIZED_COMMENT = "authorized_comment";
    private static final String PARAMETER_DISABLE_NEW_COMMENT = "disable_new_comment";
    private static final String PARAMETER_ACTIVE_CAPTCHA = "active_captcha";
    private static final String PARAMETER_LIBELLE_VALIDATE_BUTTON = "libelle_validate_button";
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_ID_CATEGORY_FILTER = "id_category_filter";
    private static final String PARAMETER_ID_TYPE_SUGGEST = "id_type";
    private static final String PARAMETER_ID_SUGGEST_SUBMIT_STATE = "id_suggest_submit_state";
    private static final String PARAMETER_ID_SUGGEST_STATE = "id_suggest_state";
    private static final String PARAMETER_ID_SUGGEST_SUBMIT_TYPE = "id_suggest_submit_type";
    private static final String PARAMETER_ENABLE = "enable";
    private static final String PARAMETER_DISABLE = "disable";
    private static final String PARAMETER_COMMENT_VALUE = "comment_value";
    private static final String PARAMETER_DELETE = "delete";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ID_EXPRESSION = "id_expression";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE = "number_suggest_submit_in_top_score";
    private static final String PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT = "number_suggest_submit_in_top_comment";
    private static final String PARAMETER_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN = "number_suggest_submit_caracters_shown";
    private static final String PARAMETER_LIMIT_NUMBER_VOTE = "limit_number_vote";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_ID_SUGGEST_SUBMIT_SORT = "id_suggest_submit_sort";
    private static final String PARAMETER_ID_SUGGEST_SUBMIT_REPORT = "id_suggest_submit_report";
    private static final String PARAMETER_ID_COMMENT_SORT = "id_comment_sort";
    private static final String PARAMETER_ID_COMMENT_STATE = "id_comment_state";
    private static final String PARAMETER_ID_CONTAINS_SUB_COMMENT_DISABLE = "id_contains_sub_comment_disable";
    private static final String PARAMETER_SHOW_CATEGORY_BLOCK = "show_category_block";
    private static final String PARAMETER_SHOW_TOP_SCORE_BLOCK = "show_top_score_block";
    private static final String PARAMETER_SHOW_TOP_COMMENT_BLOCK = "show_top_comment_block";
    private static final String PARAMETER_ACTIVE_SUGGEST_SUBMIT_PAGINATOR = "active_suggest_submit_paginator";
    private static final String PARAMETER_NUMBER_SUGGEST_SUBMIT_PER_PAGE = "number_suggest_submit_per_page";
    private static final String PARAMETER_ROLE = "role";
    private static final String PARAMETER_HEADER = "header";
    private static final String CONSTANTE_YES_VALUE = "1";
    private static final String CONSTANTE_NO_VALUE = "0";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_SUGGEST_SUBMIT_ORDER = "suggest_submit_order";
    private static final String PARAMETER_FIRST_DATE_FILTER = "first_date_filter";
    private static final String PARAMETER_LAST_DATE_FILTER = "last_date_filter";
    private static final String PARAMETER_THEME_XPAGE = "id_theme_list";
    private static final String PARAMETER_CONFIRMATION_MESSAGE = "confirmation_message";
    private static final String PARAMETER_ACTIVE_EDITOR_BBCODE_ON_COMMENT = "active_editor_bbcode_on_comment";
    private static final String PARAMETER_ID_DEFAULT_SORT = "id_default_sort";
    private static final String PARAMETER_ID_DEFAULT_SUGGEST = "id_default_suggest";
    private static final String PARAMETER_SELECTED_SUGGEST_SUBMIT = "selected_suggest_submit";
    private static final String PARAMETER_DISABLE_VOTE = "disable_vote";
    private static final String PARAMETER_DISABLE_COMMENT = "disable_comment";
    private static final String PARAMETER_ENABLE_PIN = "enable_pin";
    private static final String PARAMETER_ENABLE_REPORTS = "enable_reports";
    private static final String PARAMETER_ENABLE_TERMS_OF_USE = "enable_terms_of_use";
    private static final String PARAMETER_TERMS_OF_USE = "terms_of_use";
    private static final String PARAMETER_DISPLAY_COMMENT_IN_SUGGEST_SUBMIT_LIST = "display_comment_in_suggest_submit_list";
    private static final String PARAMETER_NUMBER_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST = "number_comment_display_in_suggest_submit_list";
    private static final String PARAMETER_NUMBER_CHAR_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST = "number_char_comment_display_in_suggest_submit_list";
    private static final String PARAMETER_UPDATE_FILE = "update_file";
    private static final String PARAMETER_IMAGE_SOURCE = "image_source";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_ID_CONTAINS_COMMENT_DISABLE = "id_contains_comment_disable";
    private static final String PARAMETER_NOTIFICATION_NEW_COMMENT_SENDER_NAME = "notification_new_comment_sender_name";
    private static final String PARAMETER_NOTIFICATION_NEW_COMMENT_TITLE = "notification_new_comment_title";
    private static final String PARAMETER_NOTIFICATION_NEW_COMMENT_BODY = "notification_new_comment_body";
    private static final String PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_SENDER_NAME = "notification_new_suggest_submit_sender_name";
    private static final String PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_TITLE = "notification_new_suggest_submit_title";
    private static final String PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_BODY = "notification_new_suggest_submit_body";
    // other constants
    private static final String EMPTY_STRING = "";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";

    // session fields
    private SuggestAdminSearchFields _searchFields = new SuggestAdminSearchFields( );
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexSuggest;
    private int _nItemsPerPageSuggest;
    private String _strCurrentPageIndexSuggestSubmit;
    private String _strCurrentPageIndexCommentSubmit;
    private int _nItemsPerPageSuggestSubmit;
    private String _strCurrentPageIndexSuggestSubmitOrder;
    private int _nItemsPerPageSuggestSubmitOrder;
    private int _nItemsPerPageCommentSubmit;
    private String _strCurrentPageIndexEntry;
    private int _nItemsPerPageEntry;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private int _nIdSuggestState = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdSuggest = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdSuggestSubmit = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdCommentSort = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdCommentState = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdContainsSubCommentDisable = SuggestUtils.CONSTANT_ID_NULL;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
    private ISuggestSubmitService _suggestSubmitService = SuggestSubmitService.getService( );
    private ICommentSubmitService _commentSubmitService = CommentSubmitService.getService( );

    /*-------------------------------MANAGEMENT  SUGGEST-----------------------------*/

    /**
     * Return management Suggest( list of suggest )
     *
     * @param request
     *            The Http request
     * @return Html suggest
     */
    public String getManageSuggest( HttpServletRequest request )
    {
        AdminUser adminUser = getUser( );
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        ReferenceList refListWorkGroups;
        ReferenceList refListSuggestState;
        List<SuggestAction> listActionsForSuggestEnable;
        List<SuggestAction> listActionsForSuggestDisable;
        List<SuggestAction> listActions;

        String strWorkGroup = request.getParameter( PARAMETER_WORKGROUP );
        String strIdSuggestState = request.getParameter( PARAMETER_ID_SUGGEST_STATE );
        _strCurrentPageIndexSuggest = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexSuggest );
        _nItemsPerPageSuggest = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageSuggest, _nDefaultItemsPerPage );

        if ( ( strIdSuggestState != null ) && !strIdSuggestState.equals( EMPTY_STRING ) )
        {
            _nIdSuggestState = SuggestUtils.getIntegerParameter( strIdSuggestState );
        }

        if ( ( strWorkGroup != null ) && !strWorkGroup.equals( EMPTY_STRING ) )
        {
            _strWorkGroup = strWorkGroup;
        }

        // build Filter
        SuggestFilter filter = new SuggestFilter( );
        filter.setIdState( _nIdSuggestState );
        filter.setWorkGroup( _strWorkGroup );

        List<Suggest> listSuggest = SuggestHome.getSuggestList( filter, getPlugin( ) );
        listSuggest = (List<Suggest>) AdminWorkgroupService.getAuthorizedCollection( listSuggest, adminUser );

        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        refListSuggestState = getRefListSuggestState( locale );

        Map<String, Object> model = new HashMap<>( );
        Paginator<Suggest> paginator = new Paginator<Suggest>( listSuggest, _nItemsPerPageSuggest, getJspManageSuggest( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexSuggest );

        listActionsForSuggestEnable = SuggestActionHome.selectActionsBySuggestState( Suggest.STATE_ENABLE, plugin, locale );
        listActionsForSuggestDisable = SuggestActionHome.selectActionsBySuggestState( Suggest.STATE_DISABLE, plugin, locale );

        for ( Suggest suggest : paginator.getPageItems( ) )
        {
            if ( suggest.isActive( ) )
            {
                listActions = listActionsForSuggestEnable;
            }
            else
            {
                listActions = listActionsForSuggestDisable;
            }

            listActions = (List<SuggestAction>) RBACService.getAuthorizedActionsCollection( listActions, suggest, getUser( ) );
            suggest.setActions( listActions );
        }

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageSuggest );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_USER_WORKGROUP_SELECTED, _strWorkGroup );
        model.put( MARK_SUGGEST_STATE_REF_LIST, refListSuggestState );
        model.put( MARK_SUGGEST_STATE_SELECTED, _nIdSuggestState );

        model.put( MARK_SUGGEST_LIST, paginator.getPageItems( ) );

        boolean bPermissionAdvancedParameter = RBACService.isAuthorized( Suggest.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                SuggestResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser( ) );
        boolean bPermissionCreateSuggest = RBACService.isAuthorized( Suggest.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                SuggestResourceIdService.PERMISSION_CREATE, getUser( ) );

        model.put( MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS, bPermissionAdvancedParameter );
        model.put( MARK_PERMISSION_CREATE_SUGGEST, bPermissionCreateSuggest );

        setPageTitleProperty( PROPERTY_MANAGE_SUGGEST_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_SUGGEST, locale, model );

        // ReferenceList refMailingList;
        // refMailingList=AdminMailingListService.getMailingLists(adminUser);
        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns advanced parameters form
     *
     * @param request
     *            The Http request
     * @return Html form
     */
    public String getManageAdvancedParameters( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, SuggestResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS,
                getUser( ) ) )
        {
            return getManageSuggest( request );
        }

        Map<String, Object> model = new HashMap<>( );
        List<Suggest> listSuggest = SuggestHome.getSuggestList( new SuggestFilter( ), getPlugin( ) );
        int nIdDefaultSuggest = -1;

        for ( Suggest suggest : listSuggest )
        {
            if ( suggest.isDefaultSuggest( ) )
            {
                nIdDefaultSuggest = suggest.getIdSuggest( );

                break;
            }
        }

        model.put( MARK_SUGGEST_LIST, SuggestUtils.getRefListSuggest( listSuggest, true ) );
        model.put( MARK_ID_DEFAULT_SUGGEST, nIdDefaultSuggest );
        model.put( MARK_PERMISSION_MANAGE_EXPORT_FORMAT,
                RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, ExportFormatResourceIdService.PERMISSION_MANAGE, getUser( ) ) );
        model.put( MARK_PERMISSION_MANAGE_CATEGORY,
                RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, CategoryResourceIdService.PERMISSION_MANAGE, getUser( ) ) );
        model.put( MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE, RBACService.isAuthorized( DefaultMessage.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DefaultMessageResourceIdService.PERMISSION_MANAGE, getUser( ) ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale( ), model );

        setPageTitleProperty( PROPERTY_MANAGE_ADVANCED_PARAMETERS_PAGE_TITLE );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return management SuggestSubmit( list of suggest submit)
     *
     * @param request
     *            The Http request
     * @param response
     *            The Http response
     * @return Html suggest
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    public IPluginActionResult getManageSuggestSubmit( HttpServletRequest request, HttpServletResponse response ) throws AccessDeniedException
    {
        ReferenceList refListSuggestSumitState = initRefListSuggestSubmitState( getPlugin( ), getLocale( ) );
        ReferenceList refListAllYesNo;
        int nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_SUGGEST_SUBMIT_VALUE_SHOWN_CHARACTERS, 100 );

        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        List<SuggestSubmit> listSuggestSubmitDisplay = new ArrayList<SuggestSubmit>( );
        // display could have been an action but it's the default one an will always be here...
        _strCurrentPageIndexSuggestSubmit = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexSuggestSubmit );
        _nItemsPerPageSuggestSubmit = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageSuggestSubmit,
                _nDefaultItemsPerPage );

        if ( ( strIdSuggest != null ) && !strIdSuggest.equals( EMPTY_STRING ) )
        {
            _nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        }

        updateSearchFieldsData( request );

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, getPlugin( ) );

        if ( ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggest.getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            throw new AccessDeniedException( "Access denied" );
        }

        // fill the selected records
        String [ ] selectedSuggestSubmit = request.getParameterValues( PARAMETER_SELECTED_SUGGEST_SUBMIT );
        List<String> listSelectedSuggestSubmit;

        if ( selectedSuggestSubmit != null )
        {
            listSelectedSuggestSubmit = Arrays.asList( selectedSuggestSubmit );

            if ( AppLogService.isDebugEnabled( ) )
            {
                AppLogService.debug( "List selected suggestSubmit : " + listSelectedSuggestSubmit );
            }
        }
        else
        {
            listSelectedSuggestSubmit = new ArrayList<>( );
        }

        _searchFields.setSelectedSuggestSubmit( listSelectedSuggestSubmit );

        // first - see if there is an invoked action
        ISuggestAction action = PluginActionManager.getPluginAction( request, ISuggestAction.class );

        if ( action != null )
        {
            if ( AppLogService.isDebugEnabled( ) )
            {
                AppLogService.debug( "Processing directory action " + action.getName( ) );
            }

            return action.process( request, response, getUser( ), _searchFields );
        }

        DefaultPluginActionResult result = new DefaultPluginActionResult( );

        // build Filter
        SubmitFilter filter = SuggestUtils.getSuggestSubmitFilter( getSearchFields( ), suggest.getIdDefaultSort( ) );

        List<Integer> listIdSuggestSubmitResult;

        if ( ( getSearchFields( ).getQuery( ) != null ) && ( !getSearchFields( ).getQuery( ).trim( ).equals( SuggestUtils.EMPTY_STRING ) ) )
        {
            int nId;

            try
            {
                nId = Integer.parseInt( getSearchFields( ).getQuery( ) );
                filter.setIdSuggestSubmit( nId );
                listIdSuggestSubmitResult = _suggestSubmitService.getSuggestSubmitListId( filter, getPlugin( ) );
            }
            catch( NumberFormatException e )
            {
                // the query is not the id of the suggest submit
                listIdSuggestSubmitResult = SuggestSearchService.getInstance( ).getSearchResults( getSearchFields( ).getQuery( ), filter, getPlugin( ) );
            }
        }
        else
        {
            listIdSuggestSubmitResult = _suggestSubmitService.getSuggestSubmitListId( filter, getPlugin( ) );
        }

        ReferenceList refListSuggestSort = SuggestUtils.getRefListSuggestSort( getLocale( ) );
        refListAllYesNo = getRefListAllYesNo( getLocale( ) );

        Map<String, Object> model = new HashMap<>( );
        Paginator<Integer> paginator = new Paginator<Integer>( listIdSuggestSubmitResult, _nItemsPerPageSuggestSubmit, getJspManageSuggestSubmit( request ),
                PARAMETER_PAGE_INDEX, _strCurrentPageIndexSuggestSubmit );
        SuggestSubmit suggestSubmit;

        for ( Object idSuggestSubmitDisplay : paginator.getPageItems( ) )
        {
            suggestSubmit = _suggestSubmitService.findByPrimaryKey( (Integer) idSuggestSubmitDisplay, false, getPlugin( ) );
            listSuggestSubmitDisplay.add( suggestSubmit );
        }

        if ( ( suggest.getCategories( ) != null ) && !suggest.getCategories( ).isEmpty( ) )
        {
            ReferenceList refCategoryList = SuggestUtils.getRefListCategory( suggest.getCategories( ) );
            SuggestUtils.addEmptyItem( refCategoryList );
            model.put( MARK_CATEGORY_LIST, refCategoryList );
            model.put( MARK_SUGGEST_SUBMIT_CATEGORY_SELECTED, getSearchFields( ).getIdCategory( ) );
        }

        if ( ( suggest.getSuggestSubmitTypes( ) != null ) && !suggest.getSuggestSubmitTypes( ).isEmpty( ) )
        {
            ReferenceList refSuggestSubmitTypes = SuggestUtils.getRefListType( suggest.getSuggestSubmitTypes( ) );
            SuggestUtils.addEmptyItem( refSuggestSubmitTypes );
            model.put( MARK_SUGGEST_SUBMIT_TYPE_LIST, refSuggestSubmitTypes );
            model.put( MARK_SUGGEST_SUBMIT_TYPE_SELECTED, getSearchFields( ).getIdType( ) );
        }

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageSuggestSubmit );
        model.put( MARK_SUGGEST_SUBMIT_STATE_REF_LIST, refListSuggestSumitState );
        model.put( MARK_SUGGEST_SUBMIT_STATE_SELECTED, getSearchFields( ).getIdSuggestSumitState( ) );
        model.put( MARK_SUGGEST_SUBMIT_LIST, listSuggestSubmitDisplay );
        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_DISABLE_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_DISABLE );
        model.put( MARK_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_PUBLISH );
        model.put( MARK_WAITING_FOR_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_WAITING_FOR_PUBLISH );
        model.put( MARK_NUMBER_SHOWN_CHARACTERS, nNumberShownCharacters );
        model.put( MARK_LIST_SUGGEST_SUBMIT_SORT, refListSuggestSort );
        model.put( MARK_SUGGEST_SUBMIT_SORT_SELECTED, getSearchFields( ).getIdSuggestSubmitSort( ) );
        model.put( MARK_REPORT_REF_LIST, refListAllYesNo );
        model.put( MARK_REPORT_SELECTED, getSearchFields( ).getIdSuggestSubmitReport( ) );
        model.put( MARK_CONTAINS_COMMENT_DISABLE_LIST, refListAllYesNo );
        model.put( MARK_CONTAINS_COMMENT_DISABLE_SELECTED, getSearchFields( ).getIdSuggestSubmitContainsCommentDisable( ) );
        model.put( MARK_QUERY, getSearchFields( ).getQuery( ) );

        PluginActionManager.fillModel( request, getUser( ), model, ISuggestAction.class, MARK_SUGGEST_ACTIONS );

        setPageTitleProperty( PROPERTY_MANAGE_SUGGEST_SUBMIT_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_SUGGEST_SUBMIT, getLocale( ), model );

        // ReferenceList refMailingList;
        // refMailingList=AdminMailingListService.getMailingLists(adminUser);
        result.setHtmlContent( getAdminPage( templateList.getHtml( ) ) );

        return result;
    }

    /**
     * Returns advanced parameters form
     *
     * @param request
     *            The Http request
     * @return Html form
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    public String getManageSuggestSubmitOrder( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_SUGGEST_SUBMIT_VALUE_SHOWN_CHARACTERS, 100 );

        _strCurrentPageIndexSuggestSubmitOrder = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexSuggestSubmitOrder );
        _nItemsPerPageSuggestSubmitOrder = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageSuggestSubmitOrder,
                _nDefaultItemsPerPage );

        List<SuggestSubmit> listSuggestSubmitDisplay = new ArrayList<SuggestSubmit>( );

        if ( ( strIdSuggest != null ) && !strIdSuggest.equals( EMPTY_STRING ) )
        {
            _nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        }

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, getPlugin( ) );

        if ( ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggest.getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            throw new AccessDeniedException( "Access denied" );
        }

        // build Filter
        SubmitFilter filter = SuggestUtils.getSuggestSubmitFilter( getSearchFields( ) );
        // reinit order
        filter.setSortBy( new ArrayList<>( ) );
        SuggestUtils.initSubmitFilterBySort( filter, SubmitFilter.SORT_MANUALLY );
        SuggestUtils.initSubmitFilterBySort( filter, SubmitFilter.SORT_BY_PINNED_FIRST );

        List<Integer> listIdSuggestSubmitResult = _suggestSubmitService.getSuggestSubmitListId( filter, getPlugin( ) );

        Map<String, Object> model = new HashMap<>( );
        Paginator<Integer> paginator = new Paginator<Integer>( listIdSuggestSubmitResult, _nItemsPerPageSuggestSubmitOrder,
                getJspManageSuggestSubmitOrder( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexSuggestSubmitOrder );
        SuggestSubmit suggestSubmit;

        for ( Object idSuggestSubmitDisplay : paginator.getPageItems( ) )
        {
            suggestSubmit = _suggestSubmitService.findByPrimaryKey( (Integer) idSuggestSubmitDisplay, false, getPlugin( ) );
            listSuggestSubmitDisplay.add( suggestSubmit );
        }

        model.put( MARK_SUGGEST_SUBMIT_ORDER_LIST_PINNED, getSuggestSubmitOrderList( _nIdSuggest, true ) );
        model.put( MARK_SUGGEST_SUBMIT_ORDER_LIST, getSuggestSubmitOrderList( _nIdSuggest, false ) );
        model.put( MARK_DISABLE_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_DISABLE );
        model.put( MARK_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_PUBLISH );
        model.put( MARK_WAITING_FOR_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_WAITING_FOR_PUBLISH );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageSuggestSubmit );

        model.put( MARK_SUGGEST_SUBMIT_LIST, listSuggestSubmitDisplay );
        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_NUMBER_SHOWN_CHARACTERS, nNumberShownCharacters );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_SUGGEST_SUBMIT_ORDER, getLocale( ), model );

        setPageTitleProperty( PROPERTY_MANAGE_SUGGEST_SUBMIT_ORDER_PAGE_TITLE );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return create suggest submit form
     *
     * @param request
     *            The Http request
     * @return Html comment submit
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    public String getCreateSuggestSubmit( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );

        if ( ( strIdSuggest != null ) && !strIdSuggest.equals( EMPTY_STRING ) )
        {
            _nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        }

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, getPlugin( ) );

        if ( ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggest.getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            throw new AccessDeniedException( "Access denied" );
        }

        Map<String, Object> model = SuggestUtils.getModelHtmlForm( suggest, getPlugin( ), getLocale( ), SuggestUtils.CONSTANT_ID_NULL, true );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_CREATE_SUGGEST_SUBMIT, getLocale( ), model );

        setPageTitleProperty( PROPERTY_CREATE_SUGGEST_SUBMIT_PAGE_TITLE );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Perform the suggest creation
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateSuggestSubmit( HttpServletRequest request )
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );

        if ( ( strIdSuggest != null ) && !strIdSuggest.equals( EMPTY_STRING ) )
        {
            _nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        }

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, getPlugin( ) );

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggest.getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            List<Response> listResponse = new ArrayList<Response>( );
            String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
            String strIdType = request.getParameter( PARAMETER_ID_TYPE_SUGGEST );
            String strDisableVote = request.getParameter( PARAMETER_DISABLE_VOTE );
            String strDisableComment = request.getParameter( PARAMETER_DISABLE_COMMENT );
            String strEnablePin = request.getParameter( PARAMETER_ENABLE_PIN );
            int nIdCategory = SuggestUtils.getIntegerParameter( strIdCategory );
            int nIdType = SuggestUtils.getIntegerParameter( strIdType );

            // Check if a category is selected (in the case or the suggest has some categories)
            if ( !suggest.getCategories( ).isEmpty( ) )
            {
                if ( ( strIdCategory == null ) || strIdCategory.equals( Integer.toString( SuggestUtils.CONSTANT_ID_NULL ) ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NO_CATEGORY, AdminMessage.TYPE_STOP );
                }
            }

            // Check if a category is selected (in the case or the suggest has some type)
            if ( !suggest.getSuggestSubmitTypes( ).isEmpty( ) )
            {
                if ( ( strIdType == null ) || strIdType.equals( Integer.toString( SuggestUtils.CONSTANT_ID_NULL ) ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NO_SUGGEST_SUBMIT_TYPE_SELECTED, AdminMessage.TYPE_STOP );
                }
            }

            SuggestSubmit suggestSubmit = new SuggestSubmit( );
            suggestSubmit.setSuggest( suggest );
            suggestSubmit.setResponses( listResponse );

            FormError formError = SuggestUtils.getAllResponsesData( request, suggestSubmit, getPlugin( ), getLocale( ) );

            if ( formError != null )
            {
                if ( formError.isMandatoryError( ) )
                {
                    Object [ ] tabRequiredFields = {
                        formError.getTitleQuestion( )
                    };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_QUESTION, tabRequiredFields, AdminMessage.TYPE_STOP );
                }

                Object [ ] tabFormError = {
                        formError.getTitleQuestion( ), formError.getErrorMessage( )
                };

                return AdminMessageService.getMessageUrl( request, MESSAGE_FORM_ERROR, tabFormError, AdminMessage.TYPE_STOP );
            }

            // perform suggest submit
            if ( nIdCategory != SuggestUtils.CONSTANT_ID_NULL )
            {
                Category category = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin( ) );
                suggestSubmit.setCategory( category );
            }

            if ( nIdType != SuggestUtils.CONSTANT_ID_NULL )
            {
                SuggestSubmitType type = SuggestSubmitTypeHome.findByPrimaryKey( nIdType, getPlugin( ) );
                suggestSubmit.setSuggestSubmitType( type );
            }

            suggestSubmit.setDisableComment( strDisableComment != null );
            suggestSubmit.setDisableVote( strDisableVote != null );
            suggestSubmit.setPinned( strEnablePin != null );
            _suggestSubmitService.create( suggestSubmit, getPlugin( ), getLocale( ) );
        }

        return getJspManageSuggestSubmit( request );
    }

    /**
     * Return management CommentSubmit( list of comment submit)
     *
     * @param request
     *            The Http request
     * @return Html comment submit
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    public String getManageCommentSubmit( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );

        String strIdCommentSort = request.getParameter( PARAMETER_ID_COMMENT_SORT );
        String strIdCommentState = request.getParameter( PARAMETER_ID_COMMENT_STATE );
        String strIdContainsSubCommentDisable = request.getParameter( PARAMETER_ID_CONTAINS_SUB_COMMENT_DISABLE );

        String strCommentIdParent = request.getParameter( PARAMETER_ID_PARENT );

        if ( ( strIdCommentSort != null ) && !strIdCommentSort.equals( EMPTY_STRING ) )
        {
            _nIdCommentSort = SuggestUtils.getIntegerParameter( strIdCommentSort );
        }

        if ( ( strIdCommentState != null ) && !strIdCommentState.equals( EMPTY_STRING ) )
        {
            _nIdCommentState = SuggestUtils.getIntegerParameter( strIdCommentState );
        }

        if ( ( strIdContainsSubCommentDisable != null ) && !strIdContainsSubCommentDisable.equals( EMPTY_STRING ) )
        {
            _nIdContainsSubCommentDisable = SuggestUtils.getIntegerParameter( strIdContainsSubCommentDisable );
        }

        int nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_SUGGEST_SUBMIT_VALUE_SHOWN_CHARACTERS, 100 );
        String strIdSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT );
        _strCurrentPageIndexCommentSubmit = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexCommentSubmit );
        _nItemsPerPageCommentSubmit = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageCommentSubmit,
                _nDefaultItemsPerPage );

        if ( ( strIdSuggestSubmit != null ) && !strIdSuggestSubmit.equals( EMPTY_STRING ) )
        {
            _nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );
        }

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( _nIdSuggestSubmit, false, plugin );
        Suggest suggest = SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest( ).getIdSuggest( ), plugin );

        if ( !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            throw new AccessDeniedException( "Access denied" );
        }

        // add repoted Message
        if ( suggestSubmit.isReported( ) )
        {
            suggestSubmit.setReportedMessages( ReportedMessageHome.getReportedMessageBySuggestSubmit( suggestSubmit.getIdSuggestSubmit( ), getPlugin( ) ) );
        }

        // build Filter
        SubmitFilter filter = SuggestUtils.getSuggestSubmitFilter( getSearchFields( ) );

        SubmitFilter commentFilter = new SubmitFilter( );
        commentFilter.setIdSuggestSubmit( _nIdSuggestSubmit );
        commentFilter.setIdSuggest( _nIdSuggest );
        commentFilter.setIdContainsCommentDisable( _nIdContainsSubCommentDisable );
        commentFilter.setIdCommentSubmitState( _nIdCommentState );

        SuggestUtils.initCommentFilterBySort( commentFilter, _nIdCommentSort );

        List<CommentSubmit> listCommentSubmit = _commentSubmitService.getCommentSubmitList( commentFilter, getPlugin( ) );

        ReferenceList refListCommentSort = SuggestUtils.getRefListCommentSort( locale );

        ReferenceList refCategoryList = SuggestUtils.getRefListCategory( suggest.getCategories( ) );
        SuggestUtils.addEmptyItem( refCategoryList );

        Map<String, Object> model = new HashMap<>( );
        Paginator<CommentSubmit> paginator = new Paginator<CommentSubmit>( listCommentSubmit, _nItemsPerPageCommentSubmit,
                getJspManageCommentSubmit( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexCommentSubmit );
        ReferenceList refListAllYesNo = getRefListAllYesNo( getLocale( ) );

        model.put( MARK_COMMENT_SORT_SELECTED, _nIdCommentSort );
        model.put( MARK_COMMENT_SORT_LIST, refListCommentSort );
        model.put( MARK_COMMENT_STATE_SELECTED, _nIdCommentState );
        model.put( MARK_COMMENT_STATE_LIST, SuggestUtils.getRefListCommentState( locale ) );
        model.put( MARK_CONTAINS_SUB_COMMENT_DISABLE_SELECTED, _nIdContainsSubCommentDisable );
        model.put( MARK_CONTAINS_SUB_COMMENT_DISABLE_LIST, refListAllYesNo );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageCommentSubmit );
        model.put( MARK_COMMENT_SUBMIT_LIST, paginator.getPageItems( ) );
        model.put( MARK_SUGGEST_SUBMIT, suggestSubmit );
        model.put( MARK_ID_SUGGEST_SUBMIT_PREV, _suggestSubmitService.findPrevIdSuggestSubmitInTheList( _nIdSuggestSubmit, filter, plugin ) );
        model.put( MARK_ID_SUGGEST_SUBMIT_NEXT, _suggestSubmitService.findNextIdSuggestSubmitInTheList( _nIdSuggestSubmit, filter, plugin ) );
        model.put( MARK_DISABLE_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_DISABLE );
        model.put( MARK_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_PUBLISH );
        model.put( MARK_WAITING_FOR_PUBLISH_SUGGEST_SUBMIT_STATE_NUMBER, SuggestSubmit.STATE_WAITING_FOR_PUBLISH );
        model.put( MARK_NUMBER_SHOWN_CHARACTERS, nNumberShownCharacters );
        model.put( MARK_CATEGORY_LIST, refCategoryList );

        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_ID_PARENT, strCommentIdParent );

        setPageTitleProperty( PROPERTY_MANAGE_COMMENT_SUBMIT_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_COMMENT_SUBMIT, locale, model );

        // ReferenceList refMailingList;
        // refMailingList=AdminMailingListService.getMailingLists(adminUser);
        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the confirmation page of delete suggest submit
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete suggest submit
     */
    public String getConfirmRemoveSuggestSubmit( HttpServletRequest request )
    {
        String strIdSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        String strMessage;
        int nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );

        if ( nIdSuggestSubmit == -1 )
        {
            return getHomeUrl( request );
        }

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSuggestSubmit, false, getPlugin( ) );

        if ( ( suggestSubmit == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        strMessage = MESSAGE_CONFIRM_REMOVE_SUGGEST_SUBMIT;

        UrlItem url = new UrlItem( JSP_DO_REMOVE_SUGGEST_SUBMIT );

        if ( strComment != null )
        {
            url.addParameter( strComment, strComment );
        }

        url.addParameter( PARAMETER_ID_SUGGEST_SUBMIT, nIdSuggestSubmit );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the suggest submit supression
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveSuggestSubmit( HttpServletRequest request )
    {
        String strIdSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT );
        String strComment = request.getParameter( PARAMETER_COMMENT );
        Plugin plugin = getPlugin( );
        int nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );

        if ( nIdSuggestSubmit == -1 )
        {
            return getHomeUrl( request );
        }

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSuggestSubmit, false, getPlugin( ) );

        if ( ( suggestSubmit == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        String strUrlBack;

        if ( strComment != null )
        {
            // on r�cup�re l'url suggest submit suivant
            strUrlBack = doFindNextSuggestSubmit( request );
        }
        else
        {
            strUrlBack = getJspManageSuggestSubmit( request );
        }

        _suggestSubmitService.remove( nIdSuggestSubmit, plugin );

        return strUrlBack;
    }

    /**
     * Perform the suggest submit change state
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doChangeSuggestSubmitState( HttpServletRequest request )
    {
        String strIdSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT );
        String strStateNumber = request.getParameter( PARAMETER_STATE_NUMBER );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        Plugin plugin = getPlugin( );
        int nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );
        int nStateNumber = SuggestUtils.getIntegerParameter( strStateNumber );

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSuggestSubmit, false, plugin );
        SuggestSubmitState suggestSubmitState = SuggestSubmitStateHome.findByNumero( nStateNumber, plugin );

        if ( ( suggestSubmit == null )
                || ( suggestSubmitState == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        suggestSubmit.setSuggestSubmitState( suggestSubmitState );
        _suggestSubmitService.update( suggestSubmit, plugin );

        if ( strComment != null )
        {
            return getJspManageCommentSubmit( request );
        }

        return getJspManageSuggestSubmit( request );
    }

    /**
     * Gets the confirmation page of delete directory record
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page of delete directory record
     */
    public String getConfirmMassChangeSuggestSubmitCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        // fill the selected records
        String [ ] selectedSuggestSubmit = request.getParameterValues( PARAMETER_SELECTED_SUGGEST_SUBMIT );
        int nIdSuggestSubmit;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = SuggestUtils.getIntegerParameter( strIdCategory );
        UrlItem url = new UrlItem( JSP_DO_CHANGE_SUGGEST_SUBMIT_CATEGORY );
        url.addParameter( PARAMETER_ID_CATEGORY, nIdCategory );

        // test All ressource selected before update
        for ( String strIdSuggestSubmit : selectedSuggestSubmit )
        {
            if ( StringUtils.isNotBlank( strIdSuggestSubmit ) && StringUtils.isNumeric( strIdSuggestSubmit ) )
            {
                nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );

                SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( nIdSuggestSubmit, false, getPlugin( ) );

                if ( ( suggestSubmit == null )
                        || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, SuggestUtils.EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                                SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
                {
                    throw new AccessDeniedException( "Access denied" );
                }

                url.addParameter( PARAMETER_SELECTED_SUGGEST_SUBMIT, nIdSuggestSubmit );
            }
        }

        if ( nIdCategory != SuggestUtils.CONSTANT_ID_NULL )
        {
            Category category = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin( ) );
            Object [ ] args = {
                ( ( category == null ) || ( category.getTitle( ) == null ) ) ? "" : category.getTitle( )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_CHANGE_SUGGEST_SUBMIT_CATEGORY, args, url.getUrl( ),
                    AdminMessage.TYPE_CONFIRMATION );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUGGEST_SUBMIT_CATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the suggest submit change state
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException
     *             If the user is not authorized to acces this feature
     */
    public String doMassChangeSuggestSubmitCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        // fill the selected records
        String [ ] selectedSuggestSubmit = request.getParameterValues( PARAMETER_SELECTED_SUGGEST_SUBMIT );
        int nIdSuggestSubmit;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = SuggestUtils.getIntegerParameter( strIdCategory );
        Category categorySelected = null;

        // test All ressource selected before update
        for ( String strIdSuggestSubmit : selectedSuggestSubmit )
        {
            if ( StringUtils.isNotBlank( strIdSuggestSubmit ) && StringUtils.isNumeric( strIdSuggestSubmit ) )
            {
                nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );

                SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( nIdSuggestSubmit, false, getPlugin( ) );

                if ( ( suggestSubmit == null )
                        || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, SuggestUtils.EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                                SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
                {
                    throw new AccessDeniedException( "Access denied" );
                }
            }
        }

        if ( nIdCategory != SuggestUtils.CONSTANT_ID_NULL )
        {
            categorySelected = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin( ) );
        }

        // update all suggest submit selected
        for ( String strIdSuggestSubmittoUpdate : selectedSuggestSubmit )
        {
            if ( StringUtils.isNotBlank( strIdSuggestSubmittoUpdate ) && StringUtils.isNumeric( strIdSuggestSubmittoUpdate ) )
            {
                nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmittoUpdate );

                SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( nIdSuggestSubmit, false, getPlugin( ) );
                suggestSubmit.setCategory( categorySelected );
                SuggestSubmitService.getService( ).update( suggestSubmit, getPlugin( ) );
            }
        }

        return getJspManageSuggestSubmit( request );
    }

    /**
     * Perform the suggest submit change state
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doChangeSuggestSubmitCategory( HttpServletRequest request )
    {
        String strIdSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        Plugin plugin = getPlugin( );
        int nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );
        int nIdCategory = SuggestUtils.getIntegerParameter( strIdCategory );

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSuggestSubmit, false, plugin );
        Category category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );

        if ( ( suggestSubmit == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                        SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        suggestSubmit.setCategory( category );

        _suggestSubmitService.update( suggestSubmit, plugin );

        if ( strComment != null )
        {
            return getJspManageCommentSubmit( request );
        }

        return getJspManageSuggestSubmit( request );
    }

    /**
     * Perform the comment submit change
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doChangeCommentSubmit( HttpServletRequest request )
    {
        String strIdParentCommentSubmit = request.getParameter( PARAMETER_ID_PARENT );

        if ( request.getParameter( PARAMETER_DELETE ) != null )
        {
            String strIdCommentSubmit = request.getParameter( PARAMETER_ID_COMMENT_SUBMIT );

            String strMessage;
            int nIdCommentSubmit = SuggestUtils.getIntegerParameter( strIdCommentSubmit );

            if ( nIdCommentSubmit == -1 )
            {
                return getHomeUrl( request );
            }

            strMessage = MESSAGE_CONFIRM_REMOVE_COMMENT_SUBMIT;

            UrlItem url = new UrlItem( JSP_DO_REMOVE_COMMENT_SUBMIT );
            url.addParameter( PARAMETER_ID_COMMENT_SUBMIT, nIdCommentSubmit );
            url.addParameter( PARAMETER_ID_PARENT, strIdParentCommentSubmit );

            return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
        }
        else
            if ( request.getParameter( PARAMETER_ENABLE ) != null )
            {
                doEnableCommentSubmit( request );
            }
            else
                if ( request.getParameter( PARAMETER_DISABLE ) != null )
                {
                    doDisableCommentSubmit( request );
                }

        return getJspManageCommentSubmit( request, strIdParentCommentSubmit );
    }

    /**
     * Submit an official answer to a suggestSubmit
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doSubmitOfficialAnswer( HttpServletRequest request )
    {
        String strCommentValueSuggest = request.getParameter( PARAMETER_COMMENT_VALUE );
        int nIdSubmitSuggest = Integer.valueOf( request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT ) );
        CommentSubmit commentSubmit = new CommentSubmit( );
        Plugin plugin = getPlugin( );

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, false, plugin );

        // Check XSS characters
        if ( StringUtil.containsXssCharacters( strCommentValueSuggest ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NEW_COMMENT_SUBMIT_INVALID, SiteMessage.TYPE_STOP );
        }

        commentSubmit.setActive( true );
        suggestSubmit.setNumberCommentEnable( suggestSubmit.getNumberCommentEnable( ) + 1 );
        suggestSubmit.setNumberComment( suggestSubmit.getNumberComment( ) + 1 );
        _suggestSubmitService.update( suggestSubmit, plugin );

        commentSubmit.setDateComment( SuggestUtils.getCurrentDate( ) );
        commentSubmit.setSuggestSubmit( suggestSubmit );
        commentSubmit.setValue( strCommentValueSuggest );
        commentSubmit.setOfficialAnswer( true );

        LuteceUser user = null;

        try
        {
            user = SecurityService.getInstance( ).getRemoteUser( request );
        }
        catch( UserNotSignedException e )
        {
            AppLogService.error( "User not identified" );
        }

        if ( user != null )
        {
            commentSubmit.setLuteceUserKey( user.getName( ) );
        }

        _commentSubmitService.create( commentSubmit, plugin );

        return getJspManageCommentSubmit( request );
    }

    /**
     * Perform the comment submit supression
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveCommentSubmit( HttpServletRequest request )
    {
        String strIdCommentSubmit = request.getParameter( PARAMETER_ID_COMMENT_SUBMIT );
        Plugin plugin = getPlugin( );
        int nIdCommentSubmit = SuggestUtils.getIntegerParameter( strIdCommentSubmit );
        CommentSubmit commentSubmit = _commentSubmitService.findByPrimaryKey( nIdCommentSubmit, plugin );

        if ( commentSubmit == null )
        {
            return getJspManageSuggest( request );
        }

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( commentSubmit.getSuggestSubmit( ).getIdSuggestSubmit( ), false, plugin );

        if ( !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        _commentSubmitService.remove( nIdCommentSubmit, plugin );

        if ( commentSubmit.isActive( ) )
        {
            suggestSubmit.setNumberCommentEnable( suggestSubmit.getNumberCommentEnable( ) - 1 );
        }

        suggestSubmit.setNumberComment( suggestSubmit.getNumberComment( ) - 1 );
        _suggestSubmitService.update( suggestSubmit, plugin );

        return getJspManageCommentSubmit( request );
    }

    /**
     * disable the comment submit
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doDisableCommentSubmit( HttpServletRequest request )
    {
        String strIdCommentSubmit = request.getParameter( PARAMETER_ID_COMMENT_SUBMIT );
        Plugin plugin = getPlugin( );
        int nIdCommentSubmit = SuggestUtils.getIntegerParameter( strIdCommentSubmit );
        CommentSubmit commentSubmit = _commentSubmitService.findByPrimaryKey( nIdCommentSubmit, plugin );

        if ( commentSubmit != null )
        {
            SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( commentSubmit.getSuggestSubmit( ).getIdSuggestSubmit( ), false, plugin );

            if ( !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                    SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
            {
                return getJspManageSuggest( request );
            }

            commentSubmit.setActive( false );
            _commentSubmitService.update( commentSubmit, plugin );
            suggestSubmit.setNumberCommentEnable( suggestSubmit.getNumberCommentEnable( ) - 1 );
            _suggestSubmitService.update( suggestSubmit, plugin );
        }

        return null;
    }

    /**
     * enable the comment submit
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doEnableCommentSubmit( HttpServletRequest request )
    {
        String strIdCommentSubmit = request.getParameter( PARAMETER_ID_COMMENT_SUBMIT );
        Plugin plugin = getPlugin( );
        int nIdCommentSubmit = SuggestUtils.getIntegerParameter( strIdCommentSubmit );
        CommentSubmit commentSubmit = _commentSubmitService.findByPrimaryKey( nIdCommentSubmit, plugin );

        if ( commentSubmit != null )
        {
            SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( commentSubmit.getSuggestSubmit( ).getIdSuggestSubmit( ), false, plugin );

            if ( !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + suggestSubmit.getSuggest( ).getIdSuggest( ),
                    SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, getUser( ) ) )
            {
                return getJspManageSuggest( request );
            }

            commentSubmit.setActive( true );
            _commentSubmitService.update( commentSubmit, plugin );
            suggestSubmit.setNumberCommentEnable( suggestSubmit.getNumberCommentEnable( ) + 1 );
            _suggestSubmitService.update( suggestSubmit, plugin );
        }

        return null;
    }

    /**
     * return the url of the next suggest submit
     *
     * @param request
     *            The HTTP request
     * @return return the url of the next suggest submit
     */
    public String doFindNextSuggestSubmit( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        SubmitFilter filter = SuggestUtils.getSuggestSubmitFilter( getSearchFields( ) );
        _nIdSuggestSubmit = _suggestSubmitService.findNextIdSuggestSubmitInTheList( _nIdSuggestSubmit, filter, plugin );

        return getJspManageCommentSubmit( request );
    }

    /**
     * return the url of the prev suggest submit
     *
     * @param request
     *            The HTTP request
     * @return return the url of the next suggest submit
     */
    public String doFindPrevSuggestSubmit( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        SubmitFilter filter = SuggestUtils.getSuggestSubmitFilter( getSearchFields( ) );
        _nIdSuggestSubmit = _suggestSubmitService.findPrevIdSuggestSubmitInTheList( _nIdSuggestSubmit, filter, plugin );

        return getJspManageCommentSubmit( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the suggest specified in parameter. return null if there is no error or else return the
     * error page url
     *
     * @param request
     *            the request
     * @param suggest
     *            suggest
     *
     * @return null if there is no error or else return the error page url
     */
    private String getSuggestData( MultipartHttpServletRequest request, Suggest suggest )
    {

        String strDefaultRole = AppPropertiesService.getProperty( PROPERTY_DEFAULT_ROLE_CODE );
        String strUpdateFile = request.getParameter( PARAMETER_UPDATE_FILE );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strLibelleContribution = request.getParameter( PARAMETER_LIBELLE_CONTRIBUTION );
        String strUnavailabilityMessage = request.getParameter( PARAMETER_UNAVAILABILITY_MESSAGE );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strIdVoteType = request.getParameter( PARAMETER_ID_VOTE_TYPE );
        String strActivePropositionState = request.getParameter( PARAMETER_ACTIVE_PROPOSITION_STATE );
        String strNumberVoteRequired = request.getParameter( PARAMETER_NUMBER_VOTE_REQUIRED );
        String strNumberDayRequired = request.getParameter( PARAMETER_NUMBER_DAY_REQUIRED );
        String strNumberSuggestSubmitInTopScore = request.getParameter( PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE );
        String strNumberSuggestSubmitCaractersShown = request.getParameter( PARAMETER_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN );
        String strNumberSuggestSubmitInTopComment = request.getParameter( PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT );
        String strThemeXpage = request.getParameter( PARAMETER_THEME_XPAGE );

        String strActiveSuggestSubmitAuthentification = request.getParameter( PARAMETER_ACTIVE_SUGGEST_SUBMIT_AUTHENTIFICATION );
        String strActiveVoteAuthentification = request.getParameter( PARAMETER_ACTIVE_VOTE_AUTHENTIFICATION );
        String strActiveCommentAuthentification = request.getParameter( PARAMETER_ACTIVE_COMMENT_AUTHENTIFICATION );

        String strDisableNewSuggestSubmit = request.getParameter( PARAMETER_DISABLE_NEW_SUGGEST_SUBMIT );
        String strEnableMailNewSuggestSubmit = request.getParameter( PARAMETER_ENABLE_MAIL_NEW_SUGGEST_SUBMIT );
        String strIdMailingListSuggestSubmit = request.getParameter( PARAMETER_ID_MAILING_LIST_SUGGEST_SUBMIT );

        String strAuthorizedComment = request.getParameter( PARAMETER_AUTHORIZED_COMMENT );
        String strActiveEditorBbcode = request.getParameter( PARAMETER_ACTIVE_EDITOR_BBCODE_ON_COMMENT );
        String strDisableNewComment = request.getParameter( PARAMETER_DISABLE_NEW_COMMENT );
        String strLimitNumberVote = request.getParameter( PARAMETER_LIMIT_NUMBER_VOTE );
        String strActiveCaptcha = request.getParameter( PARAMETER_ACTIVE_CAPTCHA );
        String strLibelleValidateButton = request.getParameter( PARAMETER_LIBELLE_VALIDATE_BUTTON );
        String strShowCategoryBlock = request.getParameter( PARAMETER_SHOW_CATEGORY_BLOCK );
        String strShowTopScoreBlock = request.getParameter( PARAMETER_SHOW_TOP_SCORE_BLOCK );
        String strShowTopCommentBlock = request.getParameter( PARAMETER_SHOW_TOP_COMMENT_BLOCK );
        String strActiveSuggestSubmitPaginator = request.getParameter( PARAMETER_ACTIVE_SUGGEST_SUBMIT_PAGINATOR );
        String strNumberSuggestSubmitPerPage = request.getParameter( PARAMETER_NUMBER_SUGGEST_SUBMIT_PER_PAGE );
        String strRole = request.getParameter( PARAMETER_ROLE );
        String strHeader = request.getParameter( PARAMETER_HEADER );
        String strConfirmationMessage = request.getParameter( PARAMETER_CONFIRMATION_MESSAGE );
        String strIdDefaultSort = request.getParameter( PARAMETER_ID_DEFAULT_SORT );
        String strDisableVote = request.getParameter( PARAMETER_DISABLE_VOTE );
        String strDisplayCommentInSuggestSubmitList = request.getParameter( PARAMETER_DISPLAY_COMMENT_IN_SUGGEST_SUBMIT_LIST );
        String strNumberCommentDisplayInSuggestSubmitList = request.getParameter( PARAMETER_NUMBER_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST );
        String strNumberCharCommentDisplayInSuggestSubmitList = request.getParameter( PARAMETER_NUMBER_CHAR_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST );
        String strEnableMailNewCommentSubmit = request.getParameter( PARAMETER_ENABLE_MAIL_NEW_COMMENT_SUBMIT );
        String strEnableMailNewReportedSubmit = request.getParameter( PARAMETER_ENABLE_MAIL_NEW_REPORTED_SUBMIT );
        String strEnableTermsOfUse = request.getParameter( PARAMETER_ENABLE_TERMS_OF_USE );
        String strTermsOfUse = request.getParameter( PARAMETER_TERMS_OF_USE );
        String strEnableReports = request.getParameter( PARAMETER_ENABLE_REPORTS );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strNotificationNewCommentSenderName = request.getParameter( PARAMETER_NOTIFICATION_NEW_COMMENT_SENDER_NAME );
        String strNotificationNewCommentTitle = request.getParameter( PARAMETER_NOTIFICATION_NEW_COMMENT_TITLE );
        String strNotificationNewCommentBody = request.getParameter( PARAMETER_NOTIFICATION_NEW_COMMENT_BODY );
        String strNotificationNewSuggestSubmitSenderName = request.getParameter( PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_SENDER_NAME );
        String strNotificationNewSuggestSubmitTitle = request.getParameter( PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_TITLE );
        String strNotificationNewSuggestSubmitBody = request.getParameter( PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_BODY );

        int nIdVoteType = SuggestUtils.getIntegerParameter( strIdVoteType );
        int nIdMailingListSuggestSubmit = SuggestUtils.getIntegerParameter( strIdMailingListSuggestSubmit );
        int nNumberDayRequired = SuggestUtils.getIntegerParameter( strNumberDayRequired );
        int nNumberVoteRequired = SuggestUtils.getIntegerParameter( strNumberVoteRequired );
        int nNumberSuggestSubmitInTopScore = SuggestUtils.getIntegerParameter( strNumberSuggestSubmitInTopScore );
        int nNumberSuggestSubmitCaractersShown = SuggestUtils.getIntegerParameter( strNumberSuggestSubmitCaractersShown );
        int nNumberSuggestSubmitInTopComment = SuggestUtils.getIntegerParameter( strNumberSuggestSubmitInTopComment );
        int nNumberSuggestSubmitPerPage = SuggestUtils.getIntegerParameter( strNumberSuggestSubmitPerPage );
        int nIdDefaultSort = SuggestUtils.getIntegerParameter( strIdDefaultSort );
        int nNumberCommentDisplayInSuggestSubmitList = SuggestUtils.getIntegerParameter( strNumberCommentDisplayInSuggestSubmitList );
        int nNumberCharCommentDisplayInSuggestSubmitList = SuggestUtils.getIntegerParameter( strNumberCharCommentDisplayInSuggestSubmitList );

        String strFieldError = EMPTY_STRING;

        if ( StringUtils.isEmpty( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else
            if ( StringUtils.isEmpty( strLibelleContribution ) )
            {
                strFieldError = FIELD_LIBELLE_CONTRIBUTION;
            }

            else
                if ( StringUtils.isEmpty( strUnavailabilityMessage ) )
                {
                    strFieldError = FIELD_UNAVAILABILITY_MESSAGE;
                }
                else
                    if ( nIdVoteType == -1 )
                    {
                        strFieldError = FIELD_VOTE_TYPE;
                    }
                    else
                        if ( StringUtils.isEmpty( strNumberSuggestSubmitCaractersShown ) )
                        {
                            strFieldError = FIELD_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN;
                        }
                        else
                            if ( ( ( ( strShowTopScoreBlock != null ) && strShowTopScoreBlock.trim( ).equals( CONSTANTE_YES_VALUE ) ) && ( strNumberSuggestSubmitInTopScore == null ) )
                                    || strNumberSuggestSubmitInTopScore.trim( ).equals( EMPTY_STRING ) )
                            {
                                strFieldError = FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE;
                            }

                            else
                                if ( ( ( strAuthorizedComment != null ) && strAuthorizedComment.trim( ).equals( CONSTANTE_YES_VALUE ) )
                                        && ( ( strNumberSuggestSubmitInTopComment == null ) || strNumberSuggestSubmitInTopComment.trim( ).equals( EMPTY_STRING ) ) )
                                {
                                    strFieldError = FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT;
                                }

                                else
                                    if ( ( ( strActiveSuggestSubmitPaginator != null ) && strActiveSuggestSubmitPaginator.trim( ).equals( CONSTANTE_YES_VALUE ) )
                                            && ( ( strNumberSuggestSubmitPerPage == null ) || strNumberSuggestSubmitPerPage.trim( ).equals( EMPTY_STRING ) ) )
                                    {
                                        strFieldError = FIELD_NUMBER_SUGGEST_SUBMIT_PER_PAGE;
                                    }

                                    else
                                        if ( ( strLibelleValidateButton == null ) || strLibelleValidateButton.trim( ).equals( EMPTY_STRING ) )
                                        {
                                            strFieldError = FIELD_LIBELE_VALIDATE_BUTTON;
                                        }
                                        else
                                            if ( ( ( strDisplayCommentInSuggestSubmitList != null ) && ( ( strNumberCommentDisplayInSuggestSubmitList == null ) || strNumberCommentDisplayInSuggestSubmitList
                                                    .trim( ).equals( EMPTY_STRING ) ) ) )
                                            {
                                                strFieldError = FIELD_NUMBER_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST;
                                            }
                                            else
                                                if ( ( ( strDisplayCommentInSuggestSubmitList != null ) && ( ( strNumberCharCommentDisplayInSuggestSubmitList == null ) || strNumberCharCommentDisplayInSuggestSubmitList
                                                        .trim( ).equals( EMPTY_STRING ) ) ) )
                                                {
                                                    strFieldError = FIELD_NUMBER_CHAR_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST;
                                                }

                                                else
                                                    if ( StringUtils.isEmpty( strNotificationNewCommentTitle ) )
                                                    {
                                                        strFieldError = FIELD_NOTIFICATION_NEW_COMMENT_TITLE;

                                                    }
                                                    else
                                                        if ( StringUtils.isEmpty( strNotificationNewCommentBody ) )
                                                        {
                                                            strFieldError = FIELD_NOTIFICATION_NEW_COMMENT_BODY;

                                                        }

                                                        else
                                                            if ( StringUtils.isEmpty( strNotificationNewSuggestSubmitTitle ) )
                                                            {

                                                                strFieldError = FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_TITLE;
                                                            }
                                                            else
                                                                if ( StringUtils.isEmpty( strNotificationNewSuggestSubmitBody ) )
                                                                {

                                                                    strFieldError = FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_BODY;
                                                                }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( nNumberSuggestSubmitCaractersShown < 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN, AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberSuggestSubmitInTopScore != null ) && !strNumberSuggestSubmitInTopScore.trim( ).equals( EMPTY_STRING )
                && ( nNumberSuggestSubmitInTopScore < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE, AdminMessage.TYPE_STOP );
        }

        if ( ( strActivePropositionState != null ) && strActivePropositionState.trim( ).equals( CONSTANTE_YES_VALUE ) && ( strNumberVoteRequired != null )
                && !strNumberVoteRequired.trim( ).equals( EMPTY_STRING ) && ( nNumberVoteRequired < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_VOTE_REQUIRED, AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberDayRequired != null ) && !strNumberDayRequired.trim( ).equals( EMPTY_STRING ) && ( nNumberDayRequired < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_DAY_REQUIRED, AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberSuggestSubmitInTopComment != null ) && !strNumberSuggestSubmitInTopComment.trim( ).equals( EMPTY_STRING )
                && ( nNumberSuggestSubmitInTopComment < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT, AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberSuggestSubmitPerPage != null ) && !strNumberSuggestSubmitPerPage.trim( ).equals( EMPTY_STRING ) && ( nNumberSuggestSubmitPerPage < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_PER_PAGE, AdminMessage.TYPE_STOP );
        }

        if ( ( strDisplayCommentInSuggestSubmitList != null ) && ( strNumberCommentDisplayInSuggestSubmitList != null )
                && !strNumberCommentDisplayInSuggestSubmitList.trim( ).equals( EMPTY_STRING ) && ( nNumberCommentDisplayInSuggestSubmitList < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST, AdminMessage.TYPE_STOP );
        }

        if ( ( strDisplayCommentInSuggestSubmitList != null ) && ( strNumberCharCommentDisplayInSuggestSubmitList != null )
                && !strNumberCharCommentDisplayInSuggestSubmitList.trim( ).equals( EMPTY_STRING ) && ( nNumberCommentDisplayInSuggestSubmitList < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_CHAR_COMMENT_DISPLAY_IN_SUGGEST_SUBMIT_LIST, AdminMessage.TYPE_STOP );
        }

        suggest.setTitle( strTitle );
        suggest.setLibelleContribution( strLibelleContribution );
        suggest.setUnavailabilityMessage( strUnavailabilityMessage );
        suggest.setWorkgroup( strWorkgroup );

        if ( suggest.getVoteType( ) == null )
        {
            suggest.setVoteType( new VoteType( ) );
        }

        suggest.getVoteType( ).setIdVoteType( nIdVoteType );

        if ( ( strActivePropositionState != null ) && strActivePropositionState.trim( ).equals( CONSTANTE_YES_VALUE ) )
        {
            suggest.setActiveSuggestPropositionState( true );
            suggest.setNumberVoteRequired( nNumberVoteRequired );
        }
        else
        {
            suggest.setActiveSuggestPropositionState( false );
            suggest.setNumberVoteRequired( -1 );
        }

        suggest.setNumberDayRequired( nNumberDayRequired );
        suggest.setActiveSuggestSubmitAuthentification( strActiveSuggestSubmitAuthentification != null );
        suggest.setActiveVoteAuthentification( strActiveVoteAuthentification != null );
        suggest.setActiveCommentAuthentification( strActiveCommentAuthentification != null );
        suggest.setActiveCaptcha( strActiveCaptcha != null );
        suggest.setDisableNewSuggestSubmit( strDisableNewSuggestSubmit != null );
        suggest.setEnableMailNewSuggestSubmit( strEnableMailNewSuggestSubmit != null );
        suggest.setEnableMailNewCommentSubmit( strEnableMailNewCommentSubmit != null );
        suggest.setEnableMailNewReportedSubmit( strEnableMailNewReportedSubmit != null );
        suggest.setIdMailingListSuggestSubmit( nIdMailingListSuggestSubmit );
        suggest.setAuthorizedComment( strAuthorizedComment != null );
        suggest.setDisableNewComment( strDisableNewComment != null );
        suggest.setActiveEditorBbcode( strActiveEditorBbcode != null );
        suggest.setLimitNumberVote( strLimitNumberVote != null );
        suggest.setShowCategoryBlock( strShowCategoryBlock != null );
        suggest.setShowTopScoreBlock( strShowTopScoreBlock != null );
        suggest.setShowTopCommentBlock( strShowTopCommentBlock != null );
        suggest.setActiveSuggestSubmitPaginator( strActiveSuggestSubmitPaginator != null );

        if ( strThemeXpage != null )
        {
            suggest.setCodeTheme( strThemeXpage );
        }

        suggest.setLibelleValidateButton( strLibelleValidateButton );
        suggest.setNumberSuggestSubmitInTopScore( nNumberSuggestSubmitInTopScore );
        suggest.setNumberSuggestSubmitInTopComment( nNumberSuggestSubmitInTopComment );
        suggest.setNumberSuggestSubmitCaractersShown( nNumberSuggestSubmitCaractersShown );
        suggest.setNumberSuggestSubmitPerPage( nNumberSuggestSubmitPerPage );
        if ( strDefaultRole != null && !strRole.equals( strDefaultRole ) )
        {
            suggest.setRole( strRole );
        }
        else
        {
            suggest.setRole( null );
        }
        suggest.setHeader( strHeader );
        suggest.setConfirmationMessage( strConfirmationMessage );
        suggest.setIdDefaultSort( nIdDefaultSort );
        suggest.setDisableVote( strDisableVote != null );
        suggest.setDisplayCommentInSuggestSubmitList( strDisplayCommentInSuggestSubmitList != null );
        suggest.setNumberCommentDisplayInSuggestSubmitList( nNumberCommentDisplayInSuggestSubmitList );
        suggest.setNumberCharCommentDisplayInSuggestSubmitList( nNumberCharCommentDisplayInSuggestSubmitList );
        suggest.setEnableReports( strEnableReports != null );
        suggest.setEnableTermsOfUse( strEnableTermsOfUse != null );
        suggest.setTermsOfUse( strTermsOfUse );
        suggest.setDescription( strDescription );
        suggest.setNotificationNewCommentSenderName( strNotificationNewCommentSenderName );
        suggest.setNotificationNewCommentTitle( strNotificationNewCommentTitle );
        suggest.setNotificationNewCommentBody( strNotificationNewCommentBody );
        suggest.setNotificationNewSuggestSubmitSenderName( strNotificationNewSuggestSubmitSenderName );
        suggest.setNotificationNewSuggestSubmitTitle( strNotificationNewSuggestSubmitTitle );
        suggest.setNotificationNewSuggestSubmitBody( strNotificationNewSuggestSubmitBody );

        if ( ( suggest.getIdSuggest( ) == SuggestUtils.CONSTANT_ID_NULL ) || ( strUpdateFile != null ) )
        {
            FileItem imageSource = request.getFile( PARAMETER_IMAGE_SOURCE );
            String strImageName = FileUploadService.getFileNameOnly( imageSource );

            ImageResource image = new ImageResource( );
            byte [ ] baImageSource = imageSource.get( );

            if ( ( strImageName != null ) && !strImageName.equals( "" ) )
            {
                image.setImage( baImageSource );
                image.setMimeType( imageSource.getContentType( ) );
            }

            suggest.setImage( image );
        }

        return null; // No error
    }

    /**
     * Gets the suggest creation page
     *
     * @param request
     *            The HTTP request
     * @return The suggest creation page
     */
    public String getCreateSuggest( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, SuggestResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return getManageSuggest( request );
        }

        Plugin plugin = getPlugin( );
        AdminUser adminUser = getUser( );
        Locale locale = getLocale( );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;
        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        refMailingList = new ReferenceList( );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        // Style management
        String defaultTheme = ThemesService.getGlobalTheme( );
        Collection<Theme> themes = ThemesService.getThemesList( );
        ReferenceList themesRefList = new ReferenceList( );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme( ), theme.getThemeDescription( ) );
        }

        ReferenceList refListSuggestSort = SuggestUtils.getRefListSuggestSort( locale );
        ReferenceList refVoteTypeList = initRefListVoteType( plugin, locale );
        DefaultMessage defaultMessage = DefaultMessageHome.find( plugin );
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_DEFAULT_MESSAGE, defaultMessage );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_YES_VALUE, CONSTANTE_YES_VALUE );
        model.put( MARK_NO_VALUE, CONSTANTE_NO_VALUE );
        model.put( MARK_VOTE_TYPE_LIST, refVoteTypeList );
        model.put( MARK_AUTHENTIFICATION_ENABLE, SecurityService.isAuthenticationEnable( ) );
        model.put( MARK_ROLE_LIST, RoleHome.getRolesList( ) );
        model.put( MARK_DEFAULT_VALUE_ROLE, Suggest.ROLE_NONE );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_DEFAULT_THEME, defaultTheme );
        model.put( MARK_LIST_SUGGEST_SUBMIT_SORT, refListSuggestSort );

        setPageTitleProperty( PROPERTY_CREATE_SUGGEST_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_SUGGEST, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the suggest creation
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateSuggest( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        if ( ( multipartRequest.getParameter( PARAMETER_CANCEL ) == null )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, SuggestResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            Plugin plugin = getPlugin( );
            Suggest suggest = new Suggest( );
            String strError = getSuggestData( multipartRequest, suggest );

            if ( strError != null )
            {
                return strError;
            }

            suggest.setIdSuggest( SuggestHome.create( suggest, plugin ) );

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifySuggest( request, suggest.getIdSuggest( ) );
            }

        }

        return getJspManageSuggest( request );
    }

    /**
     * Gets the suggest modification page
     *
     * @param request
     *            The HTTP request
     * @return The suggest modification page
     */
    public String getModifySuggest( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        List<IEntry> listEntry;
        int nNumberQuestion;
        EntryFilter filter;
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = -1;
        int nIdEntryFistInTheList = -1;
        int nIdEntryLastInTheList = -1;
        Suggest suggest = null;
        String strPanel = request.getParameter( PARAMETER_PANEL );

        if ( ( strIdSuggest != null ) && !strIdSuggest.equals( EMPTY_STRING ) )
        {
            nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
            suggest = SuggestHome.findByPrimaryKey( nIdSuggest, plugin );
        }

        if ( ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageSuggest( request );
        }

        // initialisation de la variable de session
        _nIdSuggest = nIdSuggest;
        filter = new EntryFilter( );
        filter.setIdSuggest( suggest.getIdSuggest( ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        if ( listEntry.size( ) > 0 )
        {
            nIdEntryFistInTheList = listEntry.get( 0 ).getIdEntry( );
            nIdEntryLastInTheList = listEntry.get( listEntry.size( ) - 1 ).getIdEntry( );
        }

        nNumberQuestion = EntryHome.getNumberEntryByFilter( filter, plugin );

        _strCurrentPageIndexEntry = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexEntry );
        _nItemsPerPageEntry = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageEntry, _nDefaultItemsPerPage );

        Paginator<IEntry> paginator = new Paginator<IEntry>( listEntry, _nItemsPerPageEntry, AppPathService.getBaseUrl( request ) + JSP_MODIFY_SUGGEST
                + "?id_suggest=" + suggest.getIdSuggest( ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexEntry );

        AdminUser adminUser = getUser( );

        Locale locale = getLocale( );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;
        ReferenceList refEntryType;

        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );

        refMailingList = new ReferenceList( );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        List<Category> listCategoriesView = CategoryHome.getList( plugin );
        List<SuggestSubmitType> listSuggestSubmitTypeView = SuggestSubmitTypeHome.getList( plugin );

        listCategoriesView.removeAll( suggest.getCategories( ) );
        listSuggestSubmitTypeView.removeAll( suggest.getSuggestSubmitTypes( ) );

        ReferenceList refCategoryList = SuggestUtils.getRefListCategory( listCategoriesView );
        ReferenceList refVoteTypeList = initRefListVoteType( plugin, locale );
        ReferenceList refListSuggestSort = SuggestUtils.getRefListSuggestSort( locale );
        ReferenceList refListSuggestSubmitType = SuggestUtils.getRefListType( listSuggestSubmitTypeView );

        EntryType entryTypeGroup = new EntryType( );
        refEntryType = initRefListEntryType( plugin, locale );

        // Style management
        Collection<Theme> themes = ThemesService.getThemesList( );
        ReferenceList themesRefList = new ReferenceList( );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme( ), theme.getThemeDescription( ) );
        }

        if ( suggest.getCodeTheme( ) == null )
        {
            suggest.setCodeTheme( ThemesService.getGlobalTheme( ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageEntry );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_ENTRY_TYPE_REF_LIST, refEntryType );
        model.put( MARK_ENTRY_TYPE_GROUP, entryTypeGroup );
        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_ENTRY_LIST, paginator.getPageItems( ) );
        model.put( MARK_NUMBER_QUESTION, nNumberQuestion );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_YES_VALUE, CONSTANTE_YES_VALUE );
        model.put( MARK_NO_VALUE, CONSTANTE_NO_VALUE );
        model.put( MARK_CATEGORY_LIST, refCategoryList );
        model.put( MARK_VOTE_TYPE_LIST, refVoteTypeList );
        model.put( MARK_SUGGEST_SUBMIT_TYPE_LIST, refListSuggestSubmitType );
        model.put( MARK_ID_ENTRY_FIRST_IN_THE_LIST, nIdEntryFistInTheList );
        model.put( MARK_ID_ENTRY_LAST_IN_THE_LIST, nIdEntryLastInTheList );
        model.put( MARK_AUTHENTIFICATION_ENABLE, SecurityService.isAuthenticationEnable( ) );
        model.put( MARK_ROLE_LIST, RoleHome.getRolesList( ) );
        model.put( MARK_DEFAULT_VALUE_ROLE, Suggest.ROLE_NONE );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_LIST_SUGGEST_SUBMIT_SORT, refListSuggestSort );
        model.put( MARK_PANEL, strPanel );
        setPageTitleProperty( PROPERTY_MODIFY_SUGGEST_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_SUGGEST, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the suggest modification
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifySuggest( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String strIdSuggest = multipartRequest.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

        if ( ( multipartRequest.getParameter( PARAMETER_CANCEL ) == null ) && ( nIdSuggest != -1 )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            Plugin plugin = getPlugin( );
            Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, plugin );
            String strError = getSuggestData( multipartRequest, suggest );

            if ( strError != null )
            {
                return strError;
            }

            SuggestHome.update( suggest, plugin );

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                String strPanel = request.getParameter( PARAMETER_PANEL );

                return getJspModifySuggest( request, suggest.getIdSuggest( ), strPanel );
            }
        }

        return getJspManageSuggest( request );
    }

    /**
     * Perform add suggest submit type association
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doInsertSuggestSubmitType( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Suggest suggest;
        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );
        int nISuggestSubmitType = SuggestUtils.getIntegerParameter( strIdSuggestSubmitType );
        SuggestSubmitType suggestSubmitType = SuggestSubmitTypeHome.findByPrimaryKey( nISuggestSubmitType, plugin );

        suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( suggestSubmitType != null ) && ( suggest != null ) && ( suggest.getSuggestSubmitTypes( ) != null )
                && !suggest.getSuggestSubmitTypes( ).contains( suggestSubmitType )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            SuggestSubmitTypeHome.createSuggestAssociation( _nIdSuggest, nISuggestSubmitType, plugin );
        }

        if ( _nIdSuggest != -1 )
        {
            return getJspModifySuggest( request, _nIdSuggest );
        }

        return getJspManageSuggest( request );
    }

    /**
     * remove SuggestSubmitType association
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveSuggestSubmitType( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Suggest suggest;
        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );
        int nISuggestSubmitType = SuggestUtils.getIntegerParameter( strIdSuggestSubmitType );
        SuggestSubmitType suggestSubmitType = SuggestSubmitTypeHome.findByPrimaryKey( nISuggestSubmitType, plugin );

        suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( suggestSubmitType != null ) && ( suggest != null )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            SuggestSubmitTypeHome.removeSuggestAssociation( _nIdSuggest, nISuggestSubmitType, plugin );
        }

        if ( _nIdSuggest != -1 )
        {
            return getJspModifySuggest( request, _nIdSuggest );
        }

        return getJspManageSuggest( request );
    }

    /**
     * Perform add a category in the suggest
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doInsertCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Category category;
        Suggest suggest;
        String strCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = SuggestUtils.getIntegerParameter( strCategory );
        category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );

        suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( category != null ) && ( suggest != null ) && ( suggest.getCategories( ) != null ) && !suggest.getCategories( ).contains( category )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            CategoryHome.createSuggestAssociation( _nIdSuggest, nIdCategory, plugin );
        }

        if ( _nIdSuggest != -1 )
        {
            return getJspModifySuggest( request, _nIdSuggest );
        }

        return getJspManageSuggest( request );
    }

    /**
     * Perform add a category in the suggest
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Category category;
        Suggest suggest;
        String strCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = SuggestUtils.getIntegerParameter( strCategory );
        category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );
        suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( category != null ) && ( suggest != null ) && ( suggest.getCategories( ) != null )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            CategoryHome.removeSuggestAssociation( _nIdSuggest, nIdCategory, plugin );
        }

        if ( _nIdSuggest != -1 )
        {
            return getJspModifySuggest( request, _nIdSuggest );
        }

        return getJspManageSuggest( request );
    }

    /**
     * Gets the confirmation page of delete suggest
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete suggest
     */
    public String getConfirmRemoveSuggest( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        String strMessage;
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

        if ( ( nIdSuggest == -1 )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_DELETE, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        SubmitFilter responseFilter = new SubmitFilter( );
        responseFilter.setIdSuggest( nIdSuggest );

        int nNumbersuggestSubmit = _suggestSubmitService.getCountSuggestSubmit( responseFilter, plugin );

        if ( nNumbersuggestSubmit == 0 )
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_SUGGEST;
        }
        else
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_SUGGEST_WITH_SUGGEST_SUBMIT;
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_SUGGEST );
        url.addParameter( PARAMETER_ID_SUGGEST, strIdSuggest );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the suggest supression
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveSuggest( HttpServletRequest request )
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        Plugin plugin = getPlugin( );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

        if ( ( nIdSuggest != -1 )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_DELETE, getUser( ) ) )
        {
            SuggestHome.remove( nIdSuggest, plugin );
        }

        return getJspManageSuggest( request );
    }

    /**
     * copy the suggest whose key is specified in the Http request
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCopySuggest( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Suggest suggest;
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

        if ( ( nIdSuggest == -1 )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_COPY, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        suggest = SuggestHome.findByPrimaryKey( nIdSuggest, plugin );

        Object [ ] tabSuggestTileCopy = {
            suggest.getTitle( )
        };
        String strTitleCopySuggest = I18nService.getLocalizedString( PROPERTY_COPY_SUGGEST_TITLE, tabSuggestTileCopy, getLocale( ) );

        if ( strTitleCopySuggest != null )
        {
            suggest.setTitle( strTitleCopySuggest );
        }

        SuggestHome.copy( suggest, plugin );

        return getJspManageSuggest( request );
    }

    /**
     * Gets the entry creation page
     *
     * @param request
     *            The HTTP request
     * @return The entry creation page
     */
    public String getCreateEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        Suggest suggest;

        entry = SuggestUtils.createEntryByType( request, plugin );
        suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( entry == null ) || ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageSuggest( request );
        }

        entry.setSuggest( suggest );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        setPageTitleProperty( PROPERTY_CREATE_QUESTION_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateCreate( ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the entry creation
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            entry = SuggestUtils.createEntryByType( request, plugin );

            Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

            if ( ( entry == null ) || ( suggest == null )
                    || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
            {
                return getJspManageSuggest( request );
            }

            String strError = entry.getRequestData( request, getLocale( ) );

            if ( strError != null )
            {
                return strError;
            }

            entry.setSuggest( suggest );
            entry.setIdEntry( EntryHome.create( entry, plugin ) );

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyEntry( request, entry.getIdEntry( ) );
            }
        }

        return getJspModifySuggest( request, _nIdSuggest );
    }

    /**
     * Gets the entry modification page
     *
     * @param request
     *            The HTTP request
     * @return The entry modification page
     */
    public String getModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        ReferenceList refListRegularExpression;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );
        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );
        List<EntryAdditionalAttribute> entryAdditionalAttributeList = EntryAdditionalAttributeHome.getList( nIdEntry, plugin );

        if ( ( entry == null ) || ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageSuggest( request );
        }

        Map<String, Object> model = new HashMap<>( );

        for ( EntryAdditionalAttribute entryAdditionalAttribute : entryAdditionalAttributeList )
        {
            model.put( entryAdditionalAttribute.getName( ), entryAdditionalAttribute.getValue( ) );
        }

        model.put( MARK_ENTRY, entry );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        Paginator paginator = entry.getPaginator( _nItemsPerPage, AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        if ( paginator != null )
        {
            model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
            model.put( MARK_NUMBER_ITEMS, paginator.getItemsCount( ) );
            model.put( MARK_LIST, paginator.getPageItems( ) );
            model.put( MARK_PAGINATOR, paginator );
        }

        refListRegularExpression = entry.getReferenceListRegularExpression( entry, plugin );

        if ( refListRegularExpression != null )
        {
            model.put( MARK_REGULAR_EXPRESSION_LIST_REF_LIST, refListRegularExpression );
        }

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        setPageTitleProperty( PROPERTY_MODIFY_QUESTION_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateModify( ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the entry modification
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( entry == null ) || ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = entry.getRequestData( request, getLocale( ) );

            if ( strError != null )
            {
                return strError;
            }

            EntryHome.update( entry, plugin );
        }

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            return getJspModifyEntry( request, nIdEntry );
        }

        return getJspModifySuggest( request, suggest.getIdSuggest( ) );
    }

    /**
     * Gets the confirmation page of delete entry
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    public String getConfirmRemoveEntry( HttpServletRequest request )
    {
        String strMessage;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );

        if ( nIdEntry == -1 )
        {
            return getHomeUrl( request );
        }

        strMessage = MESSAGE_CONFIRM_REMOVE_ENTRY;

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ENTRY );
        url.addParameter( PARAMETER_ID_ENTRY, strIdEntry + "#list" );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the entry supression
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );
        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( nIdEntry == -1 ) || ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        EntryHome.remove( nIdEntry, plugin );

        return getJspModifySuggest( request, suggest.getIdSuggest( ) );
    }

    /**
     * copy the entry whose key is specified in the Http request
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCopyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );
        IEntry entry;
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( entry == null ) || ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        Object [ ] tabEntryTileCopy = {
            entry.getTitle( )
        };
        String strTitleCopyEntry = I18nService.getLocalizedString( PROPERTY_COPY_ENTRY_TITLE, tabEntryTileCopy, getLocale( ) );

        if ( strTitleCopyEntry != null )
        {
            entry.setTitle( strTitleCopyEntry );
        }

        EntryHome.copy( entry, plugin );

        return getJspModifySuggest( request, suggest.getIdSuggest( ) );
    }

    /**
     * Move up the entry
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveUpEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( entry == null ) || ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setIdSuggest( entry.getSuggest( ).getIdSuggest( ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        int nIndexEntry = SuggestUtils.getIndexEntryInTheEntryList( nIdEntry, listEntry );

        if ( nIndexEntry != 0 )
        {
            int nNewPosition;
            IEntry entryToInversePosition;
            entryToInversePosition = listEntry.get( nIndexEntry - 1 );
            entryToInversePosition = EntryHome.findByPrimaryKey( entryToInversePosition.getIdEntry( ), plugin );
            nNewPosition = entryToInversePosition.getPosition( );
            entryToInversePosition.setPosition( entry.getPosition( ) );
            entry.setPosition( nNewPosition );
            EntryHome.update( entry, plugin );
            EntryHome.update( entryToInversePosition, plugin );
        }

        return getJspModifySuggest( request, suggest.getIdSuggest( ) );
    }

    /**
     * Move down the entry
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveDownEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( entry == null ) || ( suggest == null )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + _nIdSuggest, SuggestResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setIdSuggest( entry.getSuggest( ).getIdSuggest( ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        int nIndexEntry = SuggestUtils.getIndexEntryInTheEntryList( nIdEntry, listEntry );

        if ( nIndexEntry != ( listEntry.size( ) - 1 ) )
        {
            int nNewPosition;
            IEntry entryToInversePosition;
            entryToInversePosition = listEntry.get( nIndexEntry + 1 );
            entryToInversePosition = EntryHome.findByPrimaryKey( entryToInversePosition.getIdEntry( ), plugin );
            nNewPosition = entryToInversePosition.getPosition( );
            entryToInversePosition.setPosition( entry.getPosition( ) );
            entry.setPosition( nNewPosition );
            EntryHome.update( entry, plugin );
            EntryHome.update( entryToInversePosition, plugin );
        }

        return getJspModifySuggest( request, suggest.getIdSuggest( ) );
    }

    /**
     * Gets the confirmation page of disable suggest
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of disable suggest
     */
    public String getConfirmDisableSuggest( HttpServletRequest request )
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

        if ( nIdSuggest == -1 )
        {
            return getHomeUrl( request );
        }

        String strMessage;
        strMessage = MESSAGE_CONFIRM_DISABLE_SUGGEST;

        UrlItem url = new UrlItem( JSP_DO_DISABLE_SUGGEST );
        url.addParameter( PARAMETER_ID_SUGGEST, strIdSuggest );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform disable suggest
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doDisableSuggest( HttpServletRequest request )
    {
        Suggest suggest;
        Plugin plugin = getPlugin( );
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        suggest = SuggestHome.findByPrimaryKey( nIdSuggest, plugin );

        if ( ( suggest != null )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_CHANGE_STATE, getUser( ) ) )
        {
            suggest.setActive( false );
            SuggestHome.update( suggest, getPlugin( ) );

            if ( suggest.getSuggestsSubmit( ) != null )
            {
                for ( SuggestSubmit submit : suggest.getSuggestsSubmit( ) )
                {
                    String strIdSuggestSubmit = Integer.toString( submit.getIdSuggestSubmit( ) );
                    IndexationService.addIndexerAction( strIdSuggestSubmit + "_" + SuggestIndexer.SHORT_NAME,
                            AppPropertiesService.getProperty( SuggestIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );

                    SuggestIndexerUtils.addIndexerAction( strIdSuggestSubmit, IndexerAction.TASK_DELETE );
                }
            }
        }

        return getJspManageSuggest( request );
    }

    /**
     * Perform enable form
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doEnableSuggest( HttpServletRequest request )
    {
        Suggest suggest;
        Plugin plugin = getPlugin( );
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        suggest = SuggestHome.findByPrimaryKey( nIdSuggest, plugin );

        if ( ( suggest != null )
                && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_CHANGE_STATE, getUser( ) ) )
        {
            suggest.setActive( true );
            SuggestHome.update( suggest, getPlugin( ) );

            if ( suggest.getSuggestsSubmit( ) != null )
            {
                for ( SuggestSubmit submit : suggest.getSuggestsSubmit( ) )
                {
                    String strIdSuggestSubmit = Integer.toString( submit.getIdSuggestSubmit( ) );
                    IndexationService.addIndexerAction( strIdSuggestSubmit, AppPropertiesService.getProperty( SuggestIndexer.PROPERTY_INDEXER_NAME ),
                            IndexerAction.TASK_CREATE );

                    SuggestIndexerUtils.addIndexerAction( strIdSuggestSubmit, IndexerAction.TASK_CREATE );
                }
            }
        }

        return getJspManageSuggest( request );
    }

    /**
     * return url of the jsp manage suggest
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage suggest
     */
    private String getJspManageSuggest( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_SUGGEST;
    }

    // /**
    // * return url of the jsp manage suggestSubmitType
    // *
    // * @param request
    // * The HTTP request
    // * @return url of the jsp manage suggest
    // */
    // private String getJspManageSuggestSubmitType( HttpServletRequest request )
    // {
    // return AppPathService.getBaseUrl( request ) + JSP_MANAGE_SUGGEST_SUBMIT_TYPE;
    // }

    /**
     * return url of the jsp manage suggest submit
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage suggest submit
     */
    public static String getJspManageSuggestSubmit( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_SUGGEST_SUBMIT;
    }

    /**
     * return url of the jsp manage suggest submit order
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage suggest submit order
     */
    public static String getJspManageSuggestSubmitOrder( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_SUGGEST_SUBMIT_ORDER;
    }

    /**
     * return url of the jsp manage comment submit
     *
     * @param request
     *            The HTTP request
     * @param strIdParentCommentSubmit
     *            strIdParentCommentSubmit
     * @return url of the jsp manage comment submit
     */
    private String getJspManageCommentSubmit( HttpServletRequest request, String strIdParentCommentSubmit )
    {
        String strUrlReturn = ( strIdParentCommentSubmit == null ) ? JSP_MANAGE_COMMENT_SUBMIT
                : ( JSP_MANAGE_COMMENT_SUBMIT + "?" + PARAMETER_ID_PARENT + "=" + strIdParentCommentSubmit );

        return AppPathService.getBaseUrl( request ) + strUrlReturn;
    }

    /**
     * return url of the jsp manage comment submit
     *
     * @param request
     *            The HTTP request
     * @param strIdParentCommentSubmit
     *            strIdParentCommentSubmit
     * @return url of the jsp manage comment submit
     */
    private String getJspManageCommentSubmit( HttpServletRequest request )
    {
        return getJspManageCommentSubmit( request, null );
    }

    /**
     * return url of the jsp modify suggest
     *
     * @param request
     *            The HTTP request
     * @param nIdSuggest
     *            the key of suggest to modify
     * @return return url of the jsp modify suggest
     */
    private String getJspModifySuggest( HttpServletRequest request, int nIdSuggest )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_SUGGEST + "?id_suggest=" + nIdSuggest;
    }

    /**
     * return url of the jsp modify suggest
     *
     * @param request
     *            The HTTP request
     * @param nIdSuggest
     *            the key of suggest to modify
     * @param strPanel
     *            the panel anchor
     * 
     * @return return url of the jsp modify suggest
     */
    private String getJspModifySuggest( HttpServletRequest request, int nIdSuggest, String strPanel )
    {
        return !StringUtils.isEmpty( strPanel ) ? getJspModifySuggest( request, nIdSuggest ) + "&panel=" + strPanel : getJspModifySuggest( request, nIdSuggest );
    }

    /**
     * return url of the jsp modify entry
     *
     * @param request
     *            The HTTP request
     * @param nIdEntry
     *            the key of entry to modify
     * @return return url of the jsp modify entry
     */
    private String getJspModifyEntry( HttpServletRequest request, int nIdEntry )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry;
    }

    /**
     * return url of the jsp manage advanced parameters
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage advanced parameters
     */
    private String getJspManageAdvancedParameters( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_ADVANCED_PARAMETERS;
    }

    /**
     * Init reference list whidth the different entry type
     *
     * @param plugin
     *            the plugin
     * @param locale
     *            the locale
     * @return reference list of entry type
     */
    private ReferenceList initRefListEntryType( Plugin plugin, Locale locale )
    {
        ReferenceList refListEntryType = new ReferenceList( );
        List<EntryType> listEntryType = EntryTypeHome.getList( plugin );

        for ( EntryType entryType : listEntryType )
        {
            refListEntryType.addItem( entryType.getIdType( ), entryType.getTitle( ) );
        }

        return refListEntryType;
    }

    /**
     * Init reference list whidth the different vote type
     *
     * @param plugin
     *            the plugin
     * @param locale
     *            the locale
     * @return reference list of vote type
     */
    private ReferenceList initRefListVoteType( Plugin plugin, Locale locale )
    {
        ReferenceList refListVoteType = new ReferenceList( );
        List<VoteType> listVoteType = VoteTypeHome.getList( plugin );

        for ( VoteType voteType : listVoteType )
        {
            refListVoteType.addItem( voteType.getIdVoteType( ), voteType.getTitle( ) );
        }

        return refListVoteType;
    }

    /**
     * Init reference list whidth the different suggest submit state
     *
     * @param plugin
     *            the plugin
     * @param locale
     *            the locale
     * @return reference the different suggest submit state
     */
    private ReferenceList initRefListSuggestSubmitState( Plugin plugin, Locale locale )
    {
        ReferenceList refListSuggestSubmitState = new ReferenceList( );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        List<SuggestSubmitState> listSuggestSubmitState = SuggestSubmitStateHome.getList( plugin );
        refListSuggestSubmitState.addItem( -1, strAll );

        for ( SuggestSubmitState suggestSubmitState : listSuggestSubmitState )
        {
            refListSuggestSubmitState.addItem( suggestSubmitState.getIdSuggestSubmitState( ), suggestSubmitState.getTitle( ) );
        }

        return refListSuggestSubmitState;
    }

    /**
     * Delete association between entry and regular expression
     *
     * @param request
     *            the Http Request
     * @return The URL to go after performing the action
     */
    public String doRemoveRegularExpression( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );

        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );
        int nIdExpression = SuggestUtils.getIntegerParameter( strIdExpression );

        if ( ( nIdEntry != -1 ) && ( nIdExpression != -1 ) )
        {
            EntryHome.deleteVerifyBy( nIdEntry, nIdExpression, getPlugin( ) );
        }

        if ( nIdEntry != -1 )
        {
            return getJspModifyEntry( request, nIdEntry );
        }

        return getHomeUrl( request );
    }

    /**
     * insert association between entry and regular expression
     *
     * @param request
     *            the Http Request
     * @return The URL to go after performing the action
     */
    public String doInsertRegularExpression( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );

        int nIdEntry = SuggestUtils.getIntegerParameter( strIdEntry );
        int nIdExpression = SuggestUtils.getIntegerParameter( strIdExpression );

        if ( ( nIdEntry != -1 ) && ( nIdExpression != -1 ) )
        {
            EntryHome.insertVerifyBy( nIdEntry, nIdExpression, getPlugin( ) );
        }

        if ( nIdEntry != -1 )
        {
            return getJspModifyEntry( request, nIdEntry );
        }

        return getHomeUrl( request );
    }

    /**
     * get reference list of suggest state
     *
     * @param locale
     *            the locale
     * @return reference list of suggest state
     */
    private ReferenceList getRefListSuggestState( Locale locale )
    {
        ReferenceList refListState = new ReferenceList( );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        String strEnable = I18nService.getLocalizedString( PROPERTY_ENABLE, locale );
        String strDisable = I18nService.getLocalizedString( PROPERTY_DISABLE, locale );

        refListState.addItem( -1, strAll );
        refListState.addItem( 1, strEnable );
        refListState.addItem( 0, strDisable );

        return refListState;
    }

    /**
     * get reference list contains values All,Yes,No
     *
     * @param locale
     *            the locale
     * @return reference list contains values All,Yes,No
     */
    private ReferenceList getRefListAllYesNo( Locale locale )
    {
        ReferenceList refList = new ReferenceList( );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        String strYes = I18nService.getLocalizedString( PROPERTY_YES, locale );
        String strNo = I18nService.getLocalizedString( PROPERTY_NO, locale );

        refList.addItem( -1, strAll );
        refList.addItem( 1, strYes );
        refList.addItem( 0, strNo );

        return refList;
    }

    /**
     * Modify the order in the list of suggestSubmit
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    public String doModifySuggestSubmitOrder( HttpServletRequest request )
    {
        int nIdSuggestSubmit = Integer.parseInt( request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT ) );

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSuggestSubmit, false, getPlugin( ) );
        int nIdSuggest = Integer.parseInt( request.getParameter( PARAMETER_ID_SUGGEST ) );

        if ( suggestSubmit != null )
        {
            int nNewOrder = Integer.parseInt( request.getParameter( PARAMETER_SUGGEST_SUBMIT_ORDER ) );

            _suggestSubmitService.updateSuggestSubmitOrder( suggestSubmit.getSuggestSubmitOrder( ), nNewOrder, suggestSubmit.getSuggest( ).getIdSuggest( ),
                    suggestSubmit.isPinned( ), getPlugin( ) );
        }

        return getJspManageSuggestSubmitOrder( request ) + "?" + PARAMETER_ID_SUGGEST + "=" + nIdSuggest;
    }

    /**
     * Builts a list of sequence numbers
     *
     * @param nIdSuggest
     *            the id of the Suggest
     * @return the list of sequence numbers
     */
    private ReferenceList getSuggestSubmitOrderList( int nIdSuggest, boolean bListPinned )
    {
        int nMax = _suggestSubmitService.getMaxOrderList( nIdSuggest, bListPinned, getPlugin( ) );
        ReferenceList list = new ReferenceList( );

        for ( int i = 1; i < ( nMax + 1 ); i++ )
        {
            list.addItem( i, Integer.toString( i ) );
        }

        return list;
    }

    /**
     * Gets the form statistics page
     *
     * @param request
     *            the http request
     * @return the form test page
     */
    public String getStatistics( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        Timestamp tFirstDateFilter = null;
        Timestamp tLastDateFilter = null;
        SuggestFilter suggestFilter = new SuggestFilter( );
        suggestFilter.setIdState( Suggest.STATE_ENABLE );

        List<Suggest> listSuggest = SuggestHome.getSuggestList( suggestFilter, plugin );

        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        String strFirstDateFilter = request.getParameter( PARAMETER_FIRST_DATE_FILTER );
        String strLastDateFilter = request.getParameter( PARAMETER_LAST_DATE_FILTER );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_FILTER );
        String strIdType = request.getParameter( PARAMETER_ID_TYPE_SUGGEST );

        int nIdCategory = SuggestUtils.getIntegerParameter( strIdCategory );
        int nIdType = SuggestUtils.getIntegerParameter( strIdType );

        if ( ( strIdSuggest != null ) && !strIdSuggest.equals( EMPTY_STRING ) )
        {
            _nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        }
        else
            if ( listSuggest.size( ) > 0 )
            {
                _nIdSuggest = listSuggest.get( 0 ).getIdSuggest( );
            }

        Suggest suggest = SuggestHome.findByPrimaryKey( _nIdSuggest, plugin );

        if ( ( strFirstDateFilter != null ) && !strFirstDateFilter.equals( "" ) )
        {
            try
            {
                tFirstDateFilter = new Timestamp( DateUtil.formatDate( strFirstDateFilter, locale ).getTime( ) );
            }
            catch( Exception e )
            {
                tFirstDateFilter = null;
            }
        }

        if ( ( strLastDateFilter != null ) && !strLastDateFilter.equals( "" ) )
        {
            try
            {
                tLastDateFilter = new Timestamp( DateUtil.formatDate( strLastDateFilter, locale ).getTime( ) );
            }
            catch( Exception e )
            {
                tLastDateFilter = null;
            }
        }

        // build Filter
        SubmitFilter filter = new SubmitFilter( );
        filter.setIdSuggest( _nIdSuggest );
        filter.setDateFirst( tFirstDateFilter );
        filter.setDateLast( tLastDateFilter );
        filter.setIdCategory( nIdCategory );
        filter.setIdType( nIdType );

        // number of comments
        filter.setIdSuggestSubmitState( SuggestSubmit.STATE_PUBLISH );

        int nNbComments = _commentSubmitService.getCountCommentSubmit( filter, plugin );

        // number of votes
        filter.setIdSuggestSubmitState( SuggestSubmit.STATE_PUBLISH );

        int nNbVotes = 0;

        List<SuggestSubmit> listSuggestSubmit = _suggestSubmitService.getSuggestSubmitList( filter, plugin );
        List<String> listUsersKey = new ArrayList<>( );

        for ( SuggestSubmit d : listSuggestSubmit )
        {
            nNbVotes += d.getNumberVote( );

            if ( !listUsersKey.contains( d.getLuteceUserKey( ) ) )
            {
                listUsersKey.add( d.getLuteceUserKey( ) );
            }
        }

        // number of suggest submit
        filter.setIdSuggestSubmitState( SuggestSubmit.STATE_DISABLE );

        int nNbSuggestSubmitDisabled = _suggestSubmitService.getCountSuggestSubmit( filter, plugin );
        filter.setIdSuggestSubmitState( SuggestSubmit.STATE_WAITING_FOR_PUBLISH );

        int nNbSuggestSubmitWaiting = _suggestSubmitService.getCountSuggestSubmit( filter, plugin );
        filter.setIdSuggestSubmitState( SuggestSubmit.STATE_PUBLISH );

        int nNbSuggestSubmitPublished = _suggestSubmitService.getCountSuggestSubmit( filter, plugin );

        // high scores
        SuggestUtils.initSubmitFilterBySort( filter, SubmitFilter.SORT_BY_SCORE_DESC );

        int nNumberMaxSuggestSubmit = AppPropertiesService.getPropertyInt( PROPERTY_SUGGESTSUBMIT_HIGHSCORES, 10 );
        listSuggestSubmit = _suggestSubmitService.getSuggestSubmitList( filter, plugin, nNumberMaxSuggestSubmit );

        ReferenceList refSuggestList = SuggestUtils.getRefListSuggest( listSuggest, false );

        Map<String, Object> model = new HashMap<>( );

        model.put( MARK_FIRST_DATE_FILTER, ( tFirstDateFilter == null ) ? null : new Date( tFirstDateFilter.getTime( ) ) );
        model.put( MARK_LAST_DATE_FILTER, ( tLastDateFilter == null ) ? null : new Date( tLastDateFilter.getTime( ) ) );
        model.put( MARK_SUGGEST, suggest );

        if ( nNbSuggestSubmitPublished != 0 )
        {
            float fV = (float) nNbVotes / nNbSuggestSubmitPublished;
            float fC = (float) nNbComments / nNbSuggestSubmitPublished;

            BigDecimal bd = new BigDecimal( fV );
            bd = bd.setScale( 2, BigDecimal.ROUND_HALF_UP );

            BigDecimal bd2 = new BigDecimal( fC );
            bd2 = bd2.setScale( 2, BigDecimal.ROUND_HALF_UP );

            model.put( MARK_NUMBER_VOTES, bd.toString( ) );
            model.put( MARK_NUMBER_COMMENTS, bd2.toString( ) );
        }

        if ( ( suggest.getCategories( ) != null ) && !suggest.getCategories( ).isEmpty( ) )
        {
            ReferenceList refCategoryList = SuggestUtils.getRefListCategory( suggest.getCategories( ) );
            SuggestUtils.addEmptyItem( refCategoryList );
            model.put( MARK_CATEGORY_LIST, refCategoryList );
            model.put( MARK_SUGGEST_SUBMIT_CATEGORY_SELECTED, nIdCategory );

            if ( nIdCategory != SuggestUtils.CONSTANT_ID_NULL )
            {
                model.put( MARK_CATEGORY_FILTER, CategoryHome.findByPrimaryKey( nIdCategory, plugin ) );
            }
        }

        if ( ( suggest.getSuggestSubmitTypes( ) != null ) && !suggest.getSuggestSubmitTypes( ).isEmpty( ) )
        {
            ReferenceList refSuggestSubmitTypes = SuggestUtils.getRefListType( suggest.getSuggestSubmitTypes( ) );
            SuggestUtils.addEmptyItem( refSuggestSubmitTypes );
            model.put( MARK_SUGGEST_SUBMIT_TYPE_LIST, refSuggestSubmitTypes );
            model.put( MARK_SUGGEST_SUBMIT_TYPE_SELECTED, nIdType );

            if ( nIdType != SuggestUtils.CONSTANT_ID_NULL )
            {
                model.put( MARK_TYPE_FILTER, SuggestSubmitTypeHome.findByPrimaryKey( nIdType, plugin ) );
            }
        }

        model.put( MARK_NUMBER_SUGGESTSUBMIT_DISABLED, nNbSuggestSubmitDisabled );
        model.put( MARK_NUMBER_SUGGESTSUBMIT_WAITING, nNbSuggestSubmitWaiting );
        model.put( MARK_NUMBER_SUGGESTSUBMIT_PUBLISHED, nNbSuggestSubmitPublished );
        model.put( MARK_NUMBER_USERS, listUsersKey.size( ) );
        model.put( MARK_HIGH_SCORES, listSuggestSubmit );
        model.put( MARK_SUGGEST_LIST, refSuggestList );
        model.put( MARK_URL, AppPathService.getBaseUrl( request ) + JSP_MANAGE_COMMENT_SUBMIT + "?id_suggest_submit=" );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_STATISTICS_SUGGEST, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Gets the confirmation page of update all suggest submit
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of update all suggest submit
     */
    public String getConfirmUpdateAllDisplayOfSuggestSubmit( HttpServletRequest request )
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

        if ( ( nIdSuggest == -1 )
                || !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_UPDATE_ALL_SUGGEST_SUBMIT,
                        getUser( ) ) )
        {
            return getJspManageSuggest( request );
        }

        UrlItem url = new UrlItem( JSP_DO_UPDATE_ALL_SUGGEST_SUBMIT );
        url.addParameter( PARAMETER_ID_SUGGEST, strIdSuggest );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_UPDATE_ALL_SUGGEST_SUBMIT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Do update all suggest submit
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doUpdateAllDisplayOfSuggestSubmit( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

        if ( RBACService.isAuthorized( Suggest.RESOURCE_TYPE, EMPTY_STRING + nIdSuggest, SuggestResourceIdService.PERMISSION_UPDATE_ALL_SUGGEST_SUBMIT,
                getUser( ) ) )
        {
            _suggestSubmitService.updateAllDisplayOfSuggestSubmit( nIdSuggest, plugin, getLocale( ) );
        }

        return getJspManageSuggest( request );
    }

    /**
     * Do modify advanced parameters
     * 
     * @param request
     *            the request
     * @return url
     */
    public String doModifyAdvancedParameters( HttpServletRequest request )
    {
        Suggest suggest;
        Plugin plugin = getPlugin( );
        String strIdDefaultSuggest = request.getParameter( PARAMETER_ID_DEFAULT_SUGGEST );
        int nIdDefaultSuggest = SuggestUtils.getIntegerParameter( strIdDefaultSuggest );

        if ( !RBACService.isAuthorized( Suggest.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, SuggestResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS,
                getUser( ) ) )
        {
            return getManageSuggest( request );
        }

        // find default Suggest
        SuggestFilter filter = new SuggestFilter( );
        filter.setIdDefaultSuggest( SuggestFilter.ID_TRUE );

        List<Suggest> listDefaultSuggest = SuggestHome.getSuggestList( filter, plugin );

        // update default suggest
        for ( Suggest defaultSuggest : listDefaultSuggest )
        {
            suggest = SuggestHome.findByPrimaryKey( defaultSuggest.getIdSuggest( ), plugin );
            suggest.setDefaultSuggest( false );
            SuggestHome.update( suggest, plugin );
        }

        if ( nIdDefaultSuggest != SuggestUtils.CONSTANT_ID_NULL )
        {
            Suggest suggestDefault = SuggestHome.findByPrimaryKey( nIdDefaultSuggest, plugin );

            if ( suggestDefault != null )
            {
                suggestDefault.setDefaultSuggest( true );
                SuggestHome.update( suggestDefault, plugin );
            }
        }

        return getJspManageAdvancedParameters( request );
    }

    private void updateSearchFieldsData( HttpServletRequest request )
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        String strIdSuggestSumitState = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_STATE );
        String strIdSuggestSubmitSort = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_SORT );
        String strIdSuggestSubmitReport = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_REPORT );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_FILTER );
        String strIdType = request.getParameter( PARAMETER_ID_TYPE_SUGGEST );
        String strIdContainsCommentDisable = request.getParameter( PARAMETER_ID_CONTAINS_COMMENT_DISABLE );

        String strQuery = request.getParameter( PARAMETER_QUERY );

        if ( ( strIdSuggest != null ) && !strIdSuggest.equals( EMPTY_STRING ) )
        {
            getSearchFields( ).setIdSuggest( SuggestUtils.getIntegerParameter( strIdSuggest ) );
        }

        if ( ( strIdSuggestSubmitSort != null ) && !strIdSuggestSubmitSort.equals( EMPTY_STRING ) )
        {
            getSearchFields( ).setIdSuggestSubmitSort( SuggestUtils.getIntegerParameter( strIdSuggestSubmitSort ) );
        }

        if ( ( strIdSuggestSubmitReport != null ) && !strIdSuggestSubmitReport.equals( EMPTY_STRING ) )
        {
            getSearchFields( ).setIdSuggestSubmitReport( SuggestUtils.getIntegerParameter( strIdSuggestSubmitReport ) );
        }

        if ( ( strIdSuggestSumitState != null ) && !strIdSuggestSumitState.equals( EMPTY_STRING ) )
        {
            getSearchFields( ).setIdSuggestSumitState( SuggestUtils.getIntegerParameter( strIdSuggestSumitState ) );
        }

        if ( ( strIdCategory != null ) && !strIdCategory.equals( EMPTY_STRING ) )
        {
            getSearchFields( ).setIdCategory( SuggestUtils.getIntegerParameter( strIdCategory ) );
        }

        if ( ( strIdType != null ) && !strIdType.equals( EMPTY_STRING ) )
        {
            getSearchFields( ).setIdType( SuggestUtils.getIntegerParameter( strIdType ) );
        }

        if ( ( strIdContainsCommentDisable != null ) && !strIdContainsCommentDisable.equals( EMPTY_STRING ) )
        {
            getSearchFields( ).setIdSuggestSubmitContainsCommentDisable( SuggestUtils.getIntegerParameter( strIdContainsCommentDisable ) );
        }

        if ( strQuery != null )
        {
            getSearchFields( ).setQuery( strQuery );
        }
    }

    private SuggestAdminSearchFields getSearchFields( )
    {
        return _searchFields;
    }
}
