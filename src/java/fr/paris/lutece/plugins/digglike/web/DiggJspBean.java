/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.web;

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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.digglike.business.Category;
import fr.paris.lutece.plugins.digglike.business.CategoryHome;
import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.DefaultMessage;
import fr.paris.lutece.plugins.digglike.business.DefaultMessageHome;
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggAction;
import fr.paris.lutece.plugins.digglike.business.DiggActionHome;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitState;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitStateHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitType;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitTypeHome;
import fr.paris.lutece.plugins.digglike.business.EntryAdditionalAttribute;
import fr.paris.lutece.plugins.digglike.business.EntryAdditionalAttributeHome;
import fr.paris.lutece.plugins.digglike.business.EntryFilter;
import fr.paris.lutece.plugins.digglike.business.EntryHome;
import fr.paris.lutece.plugins.digglike.business.EntryType;
import fr.paris.lutece.plugins.digglike.business.EntryTypeHome;
import fr.paris.lutece.plugins.digglike.business.ExportFormat;
import fr.paris.lutece.plugins.digglike.business.FormError;
import fr.paris.lutece.plugins.digglike.business.IEntry;
import fr.paris.lutece.plugins.digglike.business.ReportedMessageHome;
import fr.paris.lutece.plugins.digglike.business.Response;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.business.VoteType;
import fr.paris.lutece.plugins.digglike.business.VoteTypeHome;
import fr.paris.lutece.plugins.digglike.service.CategoryResourceIdService;
import fr.paris.lutece.plugins.digglike.service.CommentSubmitService;
import fr.paris.lutece.plugins.digglike.service.DefaultMessageResourceIdService;
import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.digglike.service.DigglikeResourceIdService;
import fr.paris.lutece.plugins.digglike.service.DigglikeService;
import fr.paris.lutece.plugins.digglike.service.ExportFormatResourceIdService;
import fr.paris.lutece.plugins.digglike.service.ICommentSubmitService;
import fr.paris.lutece.plugins.digglike.service.IDiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.digglikesearch.DigglikeSearchService;
import fr.paris.lutece.plugins.digglike.service.search.DigglikeIndexer;
import fr.paris.lutece.plugins.digglike.utils.DiggIndexerUtils;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.plugins.digglike.web.action.DigglikeAdminSearchFields;
import fr.paris.lutece.plugins.digglike.web.action.IDigglikeAction;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.business.style.ThemeHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.PluginActionManager;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public class DiggJspBean extends PluginAdminPageJspBean
{
    public static final String MARK_DISABLE_DIGG_SUBMIT_STATE_NUMBER = "disable_state_number";
    public static final String MARK_WAITING_FOR_PUBLISH_DIGG_SUBMIT_STATE_NUMBER = "waiting_for_publish_state_number";
    public static final String MARK_PUBLISH_DIGG_SUBMIT_STATE_NUMBER = "publish_state_number";

    // templates
    private static final String TEMPLATE_MANAGE_DIGG = "admin/plugins/digglike/manage_digg.html";
    private static final String TEMPLATE_MANAGE_DIGG_SUBMIT = "admin/plugins/digglike/manage_digg_submit.html";
    private static final String TEMPLATE_CREATE_DIGG_SUBMIT = "admin/plugins/digglike/create_digg_submit.html";
    private static final String TEMPLATE_MANAGE_DIGG_SUBMIT_ORDER = "admin/plugins/digglike/manage_digg_submit_order.html";
    private static final String TEMPLATE_MANAGE_COMMENT_SUBMIT = "admin/plugins/digglike/manage_comment_submit.html";
    private static final String TEMPLATE_CREATE_DIGG = "admin/plugins/digglike/create_digg.html";
    private static final String TEMPLATE_MODIFY_DIGG = "admin/plugins/digglike/modify_digg.html";
    private static final String TEMPLATE_STATISTICS_DIGG = "admin/plugins/digglike/statistics.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/digglike/manage_advanced_parameters.html";

    // message
    private static final String MESSAGE_NEW_COMMENT_SUBMIT_INVALID = "digglike.message.newCommentSubmitInvalid";
    private static final String MESSAGE_CONFIRM_REMOVE_DIGG = "digglike.message.confirmRemoveDigg";
    private static final String MESSAGE_CONFIRM_REMOVE_DIGG_SUBMIT = "digglike.message.confirmRemoveDiggSubmit";
    private static final String MESSAGE_CONFIRM_REMOVE_COMMENT_SUBMIT = "digglike.message.confirmRemoveCommentSubmit";
    private static final String MESSAGE_CONFIRM_REMOVE_DIGG_WITH_DIGG_SUBMIT = "digglike.message.confirmRemoveDiggWithDiggSubmit";
    private static final String MESSAGE_CONFIRM_DISABLE_DIGG = "digglike.message.confirmDisableDigg";
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY = "digglike.message.confirmRemoveEntry";
    private static final String MESSAGE_CONFIRM_UPDATE_ALL_DIGG_SUBMIT = "digglike.message.confirmUpdateAllDiggSubmit";
    private static final String MESSAGE_MANDATORY_FIELD = "digglike.message.mandatory.field";
    private static final String MESSAGE_ILLOGICAL_NUMBER_VOTE_REQUIRED = "digglike.message.illogicalNumberVoteRequired";
    private static final String MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_CARACTERS_SHOWN = "digglike.message.illogicalNumberDiggSubmitCaractersShown";
    private static final String MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_IN_TOP_SCORE = "digglike.message.illogicalNumberDiggSumitInTopScore";
    private static final String MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_IN_TOP_COMMENT = "digglike.message.illogicalNumberDiggSumitInTopComment";
    private static final String MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_PER_PAGE = "digglike.message.illogicalNumberDiggSumitPerPage";
    private static final String MESSAGE_ILLOGICAL_NUMBER_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST = "digglike.message.illogicalNumberCommentDisplayInDiggSubmitList";
    private static final String MESSAGE_ILLOGICAL_NUMBER_CHAR_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST = "digglike.message.illogicalNumberCharCommentDisplayInDiggSubmitList";
    
    private static final String MESSAGE_ILLOGICAL_NUMBER_DAY_REQUIRED = "digglike.message.illogicalNumberDayRequired";
    private static final String MESSAGE_CONFIRM_CHANGE_DIGG_SUBMIT_CATEGORY="digglike.message.confirmChangeDiggSubmitCategory";
    private static final String MESSAGE_CONFIRM_REMOVE_DIGG_SUBMIT_CATEGORY="digglike.message.confirmRemoveDiggSubmitCategory";
    private static final String MESSAGE_ERROR_NO_CATEGORY = "digglike.message.errorNoCategorySelected";
    private static final String MESSAGE_ERROR_NO_DIGG_SUBMIT_TYPE_SELECTED = "digglike.message.errorNoDiggSubmitTypeSelected";
    private static final String MESSAGE_MANDATORY_QUESTION = "digglike.message.mandatory.question";
    private static final String MESSAGE_FORM_ERROR = "digglike.message.formError";
    
    private static final String FIELD_TITLE = "digglike.createDigg.labelTitle";
    private static final String FIELD_LIBELLE_CONTRIBUTION = "digglike.createDigg.labelLibelleContribution";
    private static final String FIELD_UNAVAILABILITY_MESSAGE = "digglike.createDigg.labelUnavailabilityMessage";
    private static final String FIELD_VOTE_TYPE = "digglike.createDigg.labelVoteType";
    private static final String FIELD_LIBELE_VALIDATE_BUTTON = "digglike.createDigg.labelLibelleValidateButton";
    private static final String FIELD_NUMBER_DIGG_SUBMIT_CARACTERS_SHOWN = "digglike.createDigg.labelNumberDiggSubmitCaractersShown";
    private static final String FIELD_NUMBER_DIGG_SUBMIT_IN_TOP_SCORE = "digglike.createDigg.labelNumberDiggSubmitInTopScore";
    private static final String FIELD_NUMBER_DIGG_SUBMIT_IN_TOP_COMMENT = "digglike.createDigg.labelNumberDiggSubmitInTopComment";
    private static final String FIELD_NUMBER_DIGG_SUBMIT_PER_PAGE = "digglike.createDigg.labelNumberDiggSubmitPerPage";
    private static final String FIELD_NUMBER_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST = "digglike.createDigg.labelNumberCommentDisplayInDiggSubmitList";
    private static final String FIELD_NUMBER_CHAR_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST = "digglike.createDigg.labelNumberCharCommentDisplayInDiggSubmitList";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "digglike.itemsPerPage";
    private static final String PROPERTY_ALL = "digglike.manageDigg.select.all";
    private static final String PROPERTY_YES = "digglike.manageDiggSubmit.select.yes";
    private static final String PROPERTY_NO = "digglike.manageDiggSubmit.select.no";
    private static final String PROPERTY_ENABLE = "digglike.manageDigg.select.enable";
    private static final String PROPERTY_DISABLE = "digglike.manageDigg.select.disable";
    private static final String PROPERTY_NOTHING = "digglike.createDigg.select.nothing";
    private static final String PROPERTY_MODIFY_DIGG_TITLE = "digglike.modifyDigg.title";
    private static final String PROPERTY_COPY_DIGG_TITLE = "digglike.copyDigg.title";
    private static final String PROPERTY_COPY_ENTRY_TITLE = "digglike.copyEntry.title";
    private static final String PROPERTY_CREATE_DIGG_TITLE = "digglike.createDigg.title";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = ".createEntry.titleQuestion";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "digglike.modifyEntry.titleQuestion";
    private static final String PROPERTY_NUMBER_DIGG_SUBMIT_VALUE_SHOWN_CHARACTERS = "digglike.diggSubmitValue.NumberShownCharacters";
    private static final String PROPERTY_DIGGSUBMIT_HIGHSCORES = "digglike.highscores.diggSubmit.number";
    private static final String PROPERTY_MANAGE_DIGG_PAGE_TITLE = "digglike.manageDigg.pageTitle";
    private static final String PROPERTY_MANAGE_ADVANCED_PARAMETERS_PAGE_TITLE = "digglike.manageAdvancedParameters.pageTitle";
    private static final String PROPERTY_MANAGE_DIGG_SUBMIT_PAGE_TITLE = "digglike.manageDiggSubmit.pageTitle";
    private static final String PROPERTY_MANAGE_DIGG_SUBMIT_ORDER_PAGE_TITLE = "digglike.manageDiggSubmitOrder.pageTitle";
    private static final String PROPERTY_CREATE_DIGG_SUBMIT_PAGE_TITLE = "digglike.createDiggSubmit.pageTitle";
    private static final String PROPERTY_MANAGE_COMMENT_SUBMIT_PAGE_TITLE = "digglike.manageCommentSubmit.pageTitle";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_USER_WORKGROUP_SELECTED = "user_workgroup_selected";
    private static final String MARK_DIGG_STATE_REF_LIST = "digg_state_list";
    private static final String MARK_DIGG_STATE_SELECTED = "digg_state_selected";
    private static final String MARK_DIGG_SUBMIT_STATE_REF_LIST = "digg_submit_state_list";
    private static final String MARK_DIGG_SUBMIT_STATE_SELECTED = "digg_submit_state_selected";
    private static final String MARK_MAILING_REF_LIST = "mailing_list";
    private static final String MARK_ENTRY_TYPE_REF_LIST = "entry_type_list";
    private static final String MARK_REGULAR_EXPRESSION_LIST_REF_LIST = "regular_expression_list";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_ID_ENTRY_FIRST_IN_THE_LIST = "id_entry_first_in_the_list";
    private static final String MARK_ID_ENTRY_LAST_IN_THE_LIST = "id_entry_last_in_the_list";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_DIGG_LIST = "digg_list";
    private static final String MARK_DIGG_SUBMIT_LIST = "digg_submit_list";
    private static final String MARK_COMMENT_SUBMIT_LIST = "comment_submit_list";
    private static final String MARK_VOTE_TYPE_LIST = "vote_type_list";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_DIGG_SUBMIT_TYPE_LIST = "digg_submit_type_list";
    private static final String MARK_DIGG = "digg";
    private static final String MARK_DIGG_SUBMIT = "digg_submit";
    private static final String MARK_ID_DIGG_SUBMIT_PREV = "digg_submit_prev";
    private static final String MARK_ID_DIGG_SUBMIT_NEXT = "digg_submit_next";
    private static final String MARK_PERMISSION_CREATE_DIGG = "permission_create_digg";
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
    private static final String MARK_LIST_DIGG_SUBMIT_SORT = "digg_submit_sort_list";
    private static final String MARK_DIGG_SUBMIT_SORT_SELECTED = "digg_submit_sort_selected";
    private static final String MARK_DIGG_SUBMIT_CATEGORY_SELECTED = "digg_submit_category_selected";
    private static final String MARK_DIGG_SUBMIT_TYPE_SELECTED = "digg_submit_type_selected";
    private static final String MARK_CATEGORY_FILTER = "category_filter";
    private static final String MARK_TYPE_FILTER = "type_filter";
    
    private static final String MARK_REPORT_REF_LIST = "digg_submit_report_list";
    private static final String MARK_REPORT_SELECTED = "digg_submit_report_selected";
    private static final String MARK_AUTHENTIFICATION_ENABLE = "authentification_enable";
    
    private static final String MARK_ROLE_LIST = "role_list";
    private static final String MARK_DEFAULT_VALUE_ROLE = "default_value_role";
    private static final String MARK_LIST_COMMENT_SORT = "comment_sort_list";
    private static final String MARK_COMMENT_SORT_SELECTED = "comment_sort_selected";
    private static final String MARK_DIGG_SUBMIT_ORDER_LIST = "order_list";
    private static final String MARK_DIGG_SUBMIT_ORDER_LIST_PINNED = "order_list_pinned";
    private static final String MARK_FIRST_DATE_FILTER = "first_date_filter";
    private static final String MARK_LAST_DATE_FILTER = "last_date_filter";
    private static final String MARK_NUMBER_VOTES = "number_votes";
    private static final String MARK_NUMBER_COMMENTS = "number_comments";
    private static final String MARK_NUMBER_DIGGSUBMIT_DISABLED = "number_diggsubmit_disabled";
    private static final String MARK_NUMBER_DIGGSUBMIT_WAITING = "number_diggsubmit_waiting";
    private static final String MARK_NUMBER_DIGGSUBMIT_PUBLISHED = "number_diggsubmit_published";
    private static final String MARK_HIGH_SCORES = "high_scores";
    private static final String MARK_NUMBER_USERS = "number_users";
    private static final String MARK_URL = "url";
    private static final String MARK_DEFAULT_THEME = "default_theme";
    private static final String MARK_THEME_REF_LIST = "theme_list";
    private static final String MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE = "permission_manage_default_message";
    private static final String MARK_PERMISSION_MANAGE_CATEGORY = "permission_manage_category";
    private static final String MARK_PERMISSION_MANAGE_EXPORT_FORMAT = "permission_manage_export_format";
    private static final String MARK_ID_DEFAULT_DIGG = "id_default_digg";
    private static final String MARK_QUERY = "query";
    private static final String MARK_DIGGLIKE_ACTIONS = "digglike_actions";

    // Jsp Definition
    private static final String JSP_DO_DISABLE_DIGG = "jsp/admin/plugins/digglike/DoDisableDigg.jsp";
    private static final String JSP_DO_REMOVE_DIGG = "jsp/admin/plugins/digglike/DoRemoveDigg.jsp";
    private static final String JSP_DO_REMOVE_DIGG_SUBMIT = "jsp/admin/plugins/digglike/DoRemoveDiggSubmit.jsp";
    private static final String JSP_DO_REMOVE_ENTRY = "jsp/admin/plugins/digglike/DoRemoveEntry.jsp";
    private static final String JSP_MANAGE_DIGG = "jsp/admin/plugins/digglike/ManageDigg.jsp";
    private static final String JSP_DO_REMOVE_COMMENT_SUBMIT = "jsp/admin/plugins/digglike/DoRemoveCommentSubmit.jsp";
    private static final String JSP_MANAGE_DIGG_SUBMIT_TYPE = "jsp/admin/plugins/digglike/ManageDiggSubmitType.jsp";
    private static final String JSP_MODIFY_DIGG = "jsp/admin/plugins/digglike/ModifyDigg.jsp";
    private static final String JSP_MODIFY_ENTRY = "jsp/admin/plugins/digglike/ModifyEntry.jsp";
    private static final String JSP_MANAGE_DIGG_SUBMIT = "jsp/admin/plugins/digglike/ManageDiggSubmit.jsp";
    private static final String JSP_MANAGE_DIGG_SUBMIT_ORDER = "jsp/admin/plugins/digglike/ManageDiggSubmitOrder.jsp";
    private static final String JSP_MANAGE_COMMENT_SUBMIT = "jsp/admin/plugins/digglike/ManageCommentSubmit.jsp";
    private static final String JSP_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/digglike/ManageAdvancedParameters.jsp";
    private static final String JSP_DO_UPDATE_ALL_DIGG_SUBMIT = "jsp/admin/plugins/digglike/DoUpdateAllDiggSubmit.jsp";
    private static final String JSP_DO_CHANGE_DIGG_SUBMIT_CATEGORY = "jsp/admin/plugins/digglike/DoMassChangeDiggSubmitCategory.jsp";

    // parameters form
    private static final String PARAMETER_ID_DIGG = "id_digg";
    private static final String PARAMETER_ID_DIGG_SUBMIT = "id_digg_submit";
    private static final String PARAMETER_ID_COMMENT_SUBMIT = "id_comment_submit";
    private static final String PARAMETER_ID_EXPORT_FORMAT = "id_export_format";
    private static final String PARAMETER_STATE_NUMBER = "state_number";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_LIBELLE_CONTRIBUTION = "libelle_contribution";
    private static final String PARAMETER_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_ID_VOTE_TYPE = "id_vote_type";
    private static final String PARAMETER_ACTIVE_PROPOSITION_STATE = "active_digg_proposition_state";
    private static final String PARAMETER_NUMBER_VOTE_REQUIRED = "number_vote_required";
    private static final String PARAMETER_NUMBER_DAY_REQUIRED = "number_day_required";
    private static final String PARAMETER_ACTIVE_DIGG_SUBMIT_AUTHENTIFICATION = "active_digg_submit_authentification";
    private static final String PARAMETER_ACTIVE_VOTE_AUTHENTIFICATION = "active_vote_authentification";
    private static final String PARAMETER_ACTIVE_COMMENT_AUTHENTIFICATION = "active_comment_authentification";
    private static final String PARAMETER_DISABLE_NEW_DIGG_SUBMIT = "disable_new_digg_submit";
    private static final String PARAMETER_ENABLE_MAIL_NEW_DIGG_SUBMIT = "enable_mail_new_digg_submit";
    private static final String PARAMETER_ENABLE_MAIL_NEW_COMMENT_SUBMIT = "enable_mail_new_comment_submit";
    private static final String PARAMETER_ENABLE_MAIL_NEW_REPORTED_SUBMIT = "enable_mail_new_reported_submit";
    private static final String PARAMETER_ID_MAILING_LIST_DIGG_SUBMIT = "id_mailing_list_digg_submit";
    private static final String PARAMETER_AUTHORIZED_COMMENT = "authorized_comment";
    private static final String PARAMETER_DISABLE_NEW_COMMENT = "disable_new_comment";
    private static final String PARAMETER_ACTIVE_CAPTCHA = "active_captcha";
    private static final String PARAMETER_LIBELLE_VALIDATE_BUTTON = "libelle_validate_button";
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_ID_CATEGORY_FILTER = "id_category_filter";
     
    private static final String PARAMETER_ID_TYPE_DIGG = "id_type";
    private static final String PARAMETER_ID_DIGG_SUBMIT_STATE = "id_digg_submit_state";
    private static final String PARAMETER_ID_DIGG_STATE = "id_digg_state";
    private static final String PARAMETER_ID_DIGG_SUBMIT_TYPE = "id_digg_submit_type";
    private static final String PARAMETER_ENABLE = "enable";
    private static final String PARAMETER_DISABLE = "disable";
    private static final String PARAMETER_COMMENT_VALUE = "comment_value";
    private static final String PARAMETER_DELETE = "delete";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ID_EXPRESSION = "id_expression";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_NUMBER_DIGG_SUBMIT_IN_TOP_SCORE = "number_digg_submit_in_top_score";
    private static final String PARAMETER_NUMBER_DIGG_SUBMIT_IN_TOP_COMMENT = "number_digg_submit_in_top_comment";
    private static final String PARAMETER_NUMBER_DIGG_SUBMIT_CARACTERS_SHOWN = "number_digg_submit_caracters_shown";
    private static final String PARAMETER_LIMIT_NUMBER_VOTE = "limit_number_vote";
    private static final String PARAMETER_INSERT_CATEGORY = "insert_category";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_ID_DIGG_SUBMIT_SORT = "id_digg_submit_sort";
    private static final String PARAMETER_ID_DIGG_SUBMIT_REPORT = "id_digg_submit_report";
    private static final String PARAMETER_ID_COMMENT_SORT = "id_comment_sort";
    private static final String PARAMETER_SHOW_CATEGORY_BLOCK = "show_category_block";
    private static final String PARAMETER_SHOW_TOP_SCORE_BLOCK = "show_top_score_block";
    private static final String PARAMETER_SHOW_TOP_COMMENT_BLOCK = "show_top_comment_block";
    private static final String PARAMETER_ACTIVE_DIGG_SUBMIT_PAGINATOR = "active_digg_submit_paginator";
    private static final String PARAMETER_NUMBER_DIGG_SUBMIT_PER_PAGE = "number_digg_submit_per_page";
    private static final String PARAMETER_ROLE = "role";
    private static final String PARAMETER_HEADER = "header";
    private static final String CONSTANTE_YES_VALUE = "1";
    private static final String CONSTANTE_NO_VALUE = "0";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_DIGG_SUBMIT_ORDER = "digg_submit_order";
    private static final String PARAMETER_FIRST_DATE_FILTER = "first_date_filter";
    private static final String PARAMETER_LAST_DATE_FILTER = "last_date_filter";
    private static final String PARAMETER_THEME_XPAGE = "id_theme_list";
    private static final String PARAMETER_CONFIRMATION_MESSAGE = "confirmation_message";
    private static final String PARAMETER_ACTIVE_EDITOR_BBCODE_ON_COMMENT = "active_editor_bbcode_on_comment";
    private static final String PARAMETER_ID_DEFAULT_SORT = "id_default_sort";
 
    private static final String PARAMETER_ID_DEFAULT_DIGG = "id_default_digg";
    private static final String PARAMETER_SELECTED_DIGG_SUBMIT="selected_digg_submit";
    private static final String PARAMETER_DISABLE_VOTE="disable_vote";
    private static final String PARAMETER_DISABLE_COMMENT="disable_vote";
    private static final String PARAMETER_ENABLE_PIN="enable_pin";
    
    private static final String PARAMETER_DISPLAY_COMMENT_IN_DIGG_SUBMIT_LIST="display_comment_in_digg_submit_list";
    private static final String PARAMETER_NUMBER_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST="number_comment_display_in_digg_submit_list";
    private static final String PARAMETER_NUMBER_CHAR_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST="number_char_comment_display_in_digg_submit_list";
    
    
 

    // other constants
    private static final String EMPTY_STRING = "";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";


    // private static final String EXPORT_TMPFILE_PREFIX = "exportDigg";
    // private static final String EXPORT_TMPFILE_SUFIX = ".part";
    // private static final String CONSTANT_MIME_TYPE_CSV = "application/csv";

    // session fields
    
    private DigglikeAdminSearchFields _searchFields=new DigglikeAdminSearchFields();
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexDigg;
    private int _nItemsPerPageDigg;
    private String _strCurrentPageIndexDiggSubmit;
    private String _strCurrentPageIndexCommentSubmit;
    private int _nItemsPerPageDiggSubmit;
    private String _strCurrentPageIndexDiggSubmitOrder;
    private int _nItemsPerPageDiggSubmitOrder;
    private int _nItemsPerPageCommentSubmit;
    private String _strCurrentPageIndexEntry;
    private int _nItemsPerPageEntry;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private int _nIdDiggState = -1;
    private int _nIdDigg = -1;
    private int _nIdDiggSubmit = -1;
    private int _nIdCommentSort = -1;
    //private int _nIdDiggSubmitSort = -1;
    //private int _nIdDiggSubmitReport = -1;
    //private String _strQuery = null;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
    private IDiggSubmitService _diggSubmitService = DiggSubmitService.getService(  );
    private ICommentSubmitService _commentSubmitService = CommentSubmitService.getService(  );

    /*-------------------------------MANAGEMENT  DIGG-----------------------------*/

    /**
     * Return management Digg( list of digg )
     *
     * @param request
     *            The Http request
     * @return Html digg
     */
    public String getManageDigg( HttpServletRequest request )
    {
        AdminUser adminUser = getUser(  );
        Plugin plugin = getPlugin(  );
        Locale locale = getLocale(  );
        ReferenceList refListWorkGroups;
        ReferenceList refListDiggState;
        List<DiggAction> listActionsForDiggEnable;
        List<DiggAction> listActionsForDiggDisable;
        List<DiggAction> listActions;

        String strWorkGroup = request.getParameter( PARAMETER_WORKGROUP );
        String strIdDiggState = request.getParameter( PARAMETER_ID_DIGG_STATE );
        _strCurrentPageIndexDigg = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexDigg );
        _nItemsPerPageDigg = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageDigg, _nDefaultItemsPerPage );

        if ( ( strIdDiggState != null ) && !strIdDiggState.equals( EMPTY_STRING ) )
        {
            _nIdDiggState = DiggUtils.getIntegerParameter( strIdDiggState );
        }

        if ( ( strWorkGroup != null ) && !strWorkGroup.equals( EMPTY_STRING ) )
        {
            _strWorkGroup = strWorkGroup;
        }

        // build Filter
        DiggFilter filter = new DiggFilter(  );
        filter.setIdState( _nIdDiggState );
        filter.setWorkGroup( _strWorkGroup );

        List listDigg = DiggHome.getDiggList( filter, getPlugin(  ) );
        listDigg = (List) AdminWorkgroupService.getAuthorizedCollection( listDigg, adminUser );

        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        refListDiggState = getRefListDiggState( locale );

        HashMap model = new HashMap(  );
        Paginator paginator = new Paginator( listDigg, _nItemsPerPageDigg, getJspManageDigg( request ),
                PARAMETER_PAGE_INDEX, _strCurrentPageIndexDigg );

        listActionsForDiggEnable = DiggActionHome.selectActionsByDiggState( Digg.STATE_ENABLE, plugin, locale );
        listActionsForDiggDisable = DiggActionHome.selectActionsByDiggState( Digg.STATE_DISABLE, plugin, locale );

        for ( Digg digg : (List<Digg>) paginator.getPageItems(  ) )
        {
            if ( digg.isActive(  ) )
            {
                listActions = listActionsForDiggEnable;
            }
            else
            {
                listActions = listActionsForDiggDisable;
            }

            listActions = (List<DiggAction>) RBACService.getAuthorizedActionsCollection( listActions, digg, getUser(  ) );
            digg.setActions( listActions );
        }

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageDigg );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_USER_WORKGROUP_SELECTED, _strWorkGroup );
        model.put( MARK_DIGG_STATE_REF_LIST, refListDiggState );
        model.put( MARK_DIGG_STATE_SELECTED, _nIdDiggState );

        model.put( MARK_DIGG_LIST, paginator.getPageItems(  ) );

        boolean bPermissionAdvancedParameter = RBACService.isAuthorized( Digg.RESOURCE_TYPE,
                RBAC.WILDCARD_RESOURCES_ID, DigglikeResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) );
        boolean bPermissionCreateDigg = RBACService.isAuthorized( Digg.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DigglikeResourceIdService.PERMISSION_CREATE, getUser(  ) );

        model.put( MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS, bPermissionAdvancedParameter );
        model.put( MARK_PERMISSION_CREATE_DIGG, bPermissionCreateDigg );

        setPageTitleProperty( PROPERTY_MANAGE_DIGG_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIGG, locale, model );

        // ReferenceList refMailingList;
        // refMailingList=AdminMailingListService.getMailingLists(adminUser);
        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
    * Returns advanced parameters form
    *
    * @param request The Http request
    * @return Html form
    */
    public String getManageAdvancedParameters( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Digg.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DigglikeResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            return getManageDigg( request );
        }

        HashMap model = new HashMap(  );
        List<Digg> listDigg = DiggHome.getDiggList( new DiggFilter(  ), getPlugin(  ) );
        int nIdDefaultDigg = -1;

        for ( Digg digg : listDigg )
        {
            if ( digg.isDefaultDigg(  ) )
            {
                nIdDefaultDigg = digg.getIdDigg(  );

                break;
            }
        }

        model.put( MARK_DIGG_LIST, DiggUtils.getRefListDigg( listDigg, true ) );
        model.put( MARK_ID_DEFAULT_DIGG, nIdDefaultDigg );
        model.put( MARK_PERMISSION_MANAGE_EXPORT_FORMAT,
            RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                ExportFormatResourceIdService.PERMISSION_MANAGE, getUser(  ) ) );
        model.put( MARK_PERMISSION_MANAGE_CATEGORY,
            RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                CategoryResourceIdService.PERMISSION_MANAGE, getUser(  ) ) );
        model.put( MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE,
            RBACService.isAuthorized( DefaultMessage.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DefaultMessageResourceIdService.PERMISSION_MANAGE, getUser(  ) ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale(  ),
                model );

        setPageTitleProperty( PROPERTY_MANAGE_ADVANCED_PARAMETERS_PAGE_TITLE );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return management DiggSubmit( list of digg submit)
     *
     * @param request
     *            The Http request
     * @return Html digg
     */
	public IPluginActionResult getManageDiggSubmit( HttpServletRequest request, HttpServletResponse response ) throws AccessDeniedException
    {
        
        ReferenceList refListDiggSumitState = initRefListDiggSubmitState( getPlugin(), getLocale() );
        ReferenceList refListAllYesNo;
        int nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_DIGG_SUBMIT_VALUE_SHOWN_CHARACTERS,
                100 );
    
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        List<Integer> listIdDiggSubmitResult = new ArrayList<Integer>(  );
        List<DiggSubmit> listDiggSubmitDisplay = new ArrayList<DiggSubmit>(  );
        // display could have been an action but it's the default one an will always be here...
        


        _strCurrentPageIndexDiggSubmit = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexDiggSubmit );
        _nItemsPerPageDiggSubmit = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageDiggSubmit, _nDefaultItemsPerPage );

       

        if ( ( strIdDigg != null ) && !strIdDigg.equals( EMPTY_STRING ) )
        {
            _nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        }

        updateSearchFieldsData(request);

//        if ( ( strIdDiggSubmitSort != null ) && !strIdDiggSubmitSort.equals( "-1" ) &&
//                !strIdDiggSubmitSort.equals( EMPTY_STRING ) )
//        {
//            // we can update the db
//            DiggHome.updateDiggSortField( Integer.valueOf( strIdDiggSubmitSort ), _nIdDigg, plugin );
//        }

        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, getPlugin(  ) );

        if ( ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + digg.getIdDigg(  ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
        {
        	throw new AccessDeniedException(  );
        }
        
        
        // fill the selected records
        String[] selectedDiggSubmit = request.getParameterValues( PARAMETER_SELECTED_DIGG_SUBMIT);
        List<String> listSelectedDiggSubmit;

        if ( selectedDiggSubmit != null )
        {
        	listSelectedDiggSubmit = Arrays.asList( selectedDiggSubmit );

            if ( AppLogService.isDebugEnabled(  ) )
            {
                AppLogService.debug( "List selected diggSubmit : " + listSelectedDiggSubmit );
            }
        }
        else
        {
        	listSelectedDiggSubmit = new ArrayList<String>(  );
        }

        _searchFields.setSelectedDiggSubmit( listSelectedDiggSubmit );

        // first - see if there is an invoked action
        IDigglikeAction action = PluginActionManager.getPluginAction( request, IDigglikeAction.class );

        if ( action != null )
        {
            if ( AppLogService.isDebugEnabled(  ) )
            {
                AppLogService.debug( "Processing directory action " + action.getName(  ) );
            }

            return action.process( request, response, getUser(  ), _searchFields );
        }
        else
        {
        
        
        
       DefaultPluginActionResult result = new DefaultPluginActionResult(  );

        // build Filter
        SubmitFilter filter = DiggUtils.getDiggSubmitFilter( getSearchFields() );

        if ( ( getSearchFields().getQuery() != null ) && ( getSearchFields().getQuery().trim(  ) != DiggUtils.EMPTY_STRING ) )
        {
            int nId = 0;

            try
            {
                nId = Integer.parseInt( getSearchFields().getQuery() );
                filter.setIdDiggSubmit( nId );
                listIdDiggSubmitResult = _diggSubmitService.getDiggSubmitListId( filter, getPlugin(  ) );
            }
            catch ( NumberFormatException e )
            {
                // the query is not the id of the digg submit
                listIdDiggSubmitResult = DigglikeSearchService.getInstance(  )
                                                              .getSearchResults( getSearchFields().getQuery(), filter, getPlugin(  ) );
            }
        }
        else
        {
            listIdDiggSubmitResult = _diggSubmitService.getDiggSubmitListId( filter, getPlugin(  ) );
        }

        
        ReferenceList refListDiggSort = DiggUtils.getRefListDiggSort( getLocale() );
        refListAllYesNo = getRefListAllYesNo( getLocale() );

        HashMap model = new HashMap(  );
        Paginator paginator = new Paginator( listIdDiggSubmitResult, _nItemsPerPageDiggSubmit,
                getJspManageDiggSubmit( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexDiggSubmit );
        DiggSubmit diggSubmit;

        for ( Object idDiggSubmitDisplay : paginator.getPageItems(  ) )
        {
            diggSubmit = _diggSubmitService.findByPrimaryKey( (Integer) idDiggSubmitDisplay, false, getPlugin(  ) );
            filter.setIdDiggSubmit( (Integer) idDiggSubmitDisplay );
            diggSubmit.setNumberComment( _commentSubmitService.getCountCommentSubmit( filter, getPlugin() ) );
            listDiggSubmitDisplay.add( diggSubmit );
        }
       
        
     	if(  digg.getCategories()!=null && !digg.getCategories().isEmpty() )
    	{
    		ReferenceList refCategoryList = DiggUtils.getRefListCategory( digg.getCategories(  ) );
    		DiggUtils.addEmptyItem(refCategoryList);
    		model.put( MARK_CATEGORY_LIST, refCategoryList );
    	 model.put( MARK_DIGG_SUBMIT_CATEGORY_SELECTED, getSearchFields().getIdCategory() );
    	}
     	if(  digg.getDiggSubmitTypes()!=null && !digg.getDiggSubmitTypes().isEmpty() )
    	{
    		ReferenceList refDiggSubmitTypes = DiggUtils.getRefListType(digg.getDiggSubmitTypes() );
    		DiggUtils.addEmptyItem(refDiggSubmitTypes);
    		model.put( MARK_DIGG_SUBMIT_TYPE_LIST, refDiggSubmitTypes );
    		model.put( MARK_DIGG_SUBMIT_TYPE_SELECTED, getSearchFields().getIdType() );
    	}
       
        
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageDiggSubmit );
        model.put( MARK_DIGG_SUBMIT_STATE_REF_LIST, refListDiggSumitState );
        model.put( MARK_DIGG_SUBMIT_STATE_SELECTED, getSearchFields().getIdDiggSumitState() );
        model.put( MARK_DIGG_SUBMIT_LIST, listDiggSubmitDisplay );
        model.put( MARK_DIGG, digg );
        model.put( MARK_DISABLE_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_DISABLE );
        model.put( MARK_PUBLISH_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_PUBLISH );
        model.put( MARK_WAITING_FOR_PUBLISH_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_WAITING_FOR_PUBLISH );
        model.put( MARK_NUMBER_SHOWN_CHARACTERS, nNumberShownCharacters );
        model.put( MARK_LIST_DIGG_SUBMIT_SORT, refListDiggSort );
        model.put( MARK_DIGG_SUBMIT_SORT_SELECTED, getSearchFields().getIdDiggSubmitSort() );
        model.put( MARK_REPORT_REF_LIST, refListAllYesNo );
        model.put( MARK_REPORT_SELECTED, getSearchFields().getIdDiggSubmitReport() );
        model.put( MARK_QUERY, getSearchFields().getQuery() );
        
        PluginActionManager.fillModel( request, getUser(  ), model, IDigglikeAction.class, MARK_DIGGLIKE_ACTIONS );

        setPageTitleProperty( PROPERTY_MANAGE_DIGG_SUBMIT_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIGG_SUBMIT, getLocale(), model );

        // ReferenceList refMailingList;
        // refMailingList=AdminMailingListService.getMailingLists(adminUser);
        result.setHtmlContent( getAdminPage( templateList.getHtml(  ) ) );
        
        return result; 
        }
    }
	
	
	 /**
	    * Returns advanced parameters form
	    *
	    * @param request The Http request
	    * @return Html form
	    */
	    public String getManageDiggSubmitOrder( HttpServletRequest request ) throws AccessDeniedException
	    {
	    	
	    	String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
	    	int nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_DIGG_SUBMIT_VALUE_SHOWN_CHARACTERS,
	                 100 );
	     
	    	_strCurrentPageIndexDiggSubmitOrder = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
	                _strCurrentPageIndexDiggSubmitOrder );
	        _nItemsPerPageDiggSubmitOrder = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
	                _nItemsPerPageDiggSubmitOrder, _nDefaultItemsPerPage );

	        List<Integer> listIdDiggSubmitResult = new ArrayList<Integer>(  );
	        List<DiggSubmit> listDiggSubmitDisplay = new ArrayList<DiggSubmit>(  );

	        if ( ( strIdDigg != null ) && !strIdDigg.equals( EMPTY_STRING ) )
	        {
	            _nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
	        }
	    	
	    	
	    	
	    	Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, getPlugin(  ) );
	    	if ( ( digg == null ) ||
	                 !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + digg.getIdDigg(  ),
	                     DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
	         {
	         	throw new AccessDeniedException(  );
	         }
	    	
	    	
	    	// build Filter
	        SubmitFilter filter = DiggUtils.getDiggSubmitFilter( getSearchFields() );
	        //reinit order
	        filter.setSortBy(new ArrayList<Integer>());
	        DiggUtils.initSubmitFilterBySort(filter,SubmitFilter.SORT_MANUALLY);
	    	DiggUtils.initSubmitFilterBySort(filter,SubmitFilter.SORT_BY_PINNED_FIRST);
	    	
	    	listIdDiggSubmitResult = _diggSubmitService.getDiggSubmitListId( filter, getPlugin(  ) );
	        HashMap model = new HashMap(  );
	        Paginator paginator = new Paginator( listIdDiggSubmitResult, _nItemsPerPageDiggSubmitOrder,
	                getJspManageDiggSubmitOrder( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexDiggSubmitOrder );
	        DiggSubmit diggSubmit;

	        for ( Object idDiggSubmitDisplay : paginator.getPageItems(  ) )
	        {
	            diggSubmit = _diggSubmitService.findByPrimaryKey( (Integer) idDiggSubmitDisplay, false, getPlugin(  ) );
	            filter.setIdDiggSubmit( (Integer) idDiggSubmitDisplay );
	            diggSubmit.setNumberComment( _commentSubmitService.getCountCommentSubmit( filter, getPlugin() ) );
	            listDiggSubmitDisplay.add( diggSubmit );
	        }
	        
	        
	        
	        model.put( MARK_DIGG_SUBMIT_ORDER_LIST_PINNED, getDiggSubmitOrderList( _nIdDigg,true ) );
	        model.put( MARK_DIGG_SUBMIT_ORDER_LIST, getDiggSubmitOrderList( _nIdDigg,false ) );
	        model.put( MARK_DISABLE_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_DISABLE );
	        model.put( MARK_PUBLISH_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_PUBLISH );
	        model.put( MARK_WAITING_FOR_PUBLISH_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_WAITING_FOR_PUBLISH );
	        
	        model.put( MARK_PAGINATOR, paginator );
	        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageDiggSubmit );
	        
	        model.put( MARK_DIGG_SUBMIT_LIST, listDiggSubmitDisplay );
	        model.put( MARK_DIGG, digg );
	        model.put( MARK_NUMBER_SHOWN_CHARACTERS, nNumberShownCharacters );
	        
	        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIGG_SUBMIT_ORDER, getLocale(  ),
	                model );

	        setPageTitleProperty( PROPERTY_MANAGE_DIGG_SUBMIT_ORDER_PAGE_TITLE );

	       
	        return getAdminPage( templateList.getHtml(  ) );
	    }
	    
	    
	    
	    /**
	     * Return create digg submit form
	     *
	     * @param request
	     *            The Http request
	     * @return Html comment submit
	     */
	    public String getCreateDiggSubmit( HttpServletRequest request ) throws AccessDeniedException
	    {
	    	String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
	    	if ( ( strIdDigg != null ) && !strIdDigg.equals( EMPTY_STRING ) )
	        {
	            _nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
	        }
	    	
	    	Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, getPlugin(  ) );
	    	if ( ( digg == null ) ||
	                 !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + digg.getIdDigg(  ),
	                     DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
	         {
	         	throw new AccessDeniedException(  );
	         }
	    	
	    	 Map<String, Object> model =DiggUtils.getModelHtmlForm( digg, getPlugin(), getLocale(), DiggUtils.CONSTANT_ID_NULL,true );
	    	
	    	 HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_CREATE_DIGG_SUBMIT, getLocale(  ),
		                model );

		      setPageTitleProperty( PROPERTY_CREATE_DIGG_SUBMIT_PAGE_TITLE );
		      return getAdminPage( templateList.getHtml(  ) );
	    }
	    
	    
	    
	    /**
	     * Perform the digg creation
	     *
	     * @param request
	     *            The HTTP request
	     * @return The URL to go after performing the action
	     */
	    public String doCreateDiggSubmit( HttpServletRequest request )
	    {
	    	String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
	    	
	    	if ( ( strIdDigg != null ) && !strIdDigg.equals( EMPTY_STRING ) )
	        {
	            _nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
	        }
	    	
	    	Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, getPlugin(  ) );
	   
	    	if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) &&
	        		RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + digg.getIdDigg(  ),
		                     DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
	        {
	            
	    		List<Response> listResponse = new ArrayList<Response>(  );
	    		String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
	            String strIdType = request.getParameter( PARAMETER_ID_TYPE_DIGG );
	            String strDisableVote = request.getParameter( PARAMETER_DISABLE_VOTE);
	            String strDisableComment = request.getParameter( PARAMETER_DISABLE_COMMENT );
	            String strEnablePin = request.getParameter(PARAMETER_ENABLE_PIN );
	            int nIdCategory = DiggUtils.getIntegerParameter( strIdCategory );
	            int nIdType = DiggUtils.getIntegerParameter( strIdType );
	            
	           //Check if a category is selected (in the case or the digg has some categories)
	            if (  !digg.getCategories().isEmpty())
	            {
	                if ( strIdCategory == null || strIdCategory.equals( Integer.toString( DiggUtils.CONSTANT_ID_NULL ) ))
	                {
	                	return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NO_CATEGORY, AdminMessage.TYPE_STOP);
	                }
	            }
	            //Check if a category is selected (in the case or the digg has some type)
	            if (  !digg.getDiggSubmitTypes().isEmpty())
	            {
	                if ( strIdType == null || strIdType.equals( Integer.toString( DiggUtils.CONSTANT_ID_NULL ) ))
	                {
	                	return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NO_DIGG_SUBMIT_TYPE_SELECTED, AdminMessage.TYPE_STOP);
	                }
	            }

	            DiggSubmit diggSubmit = new DiggSubmit(  );
	            diggSubmit.setDigg( digg );
	            diggSubmit.setResponses( listResponse );
	            
	            
	            FormError formError = DiggUtils.getAllResponsesData(request, diggSubmit, getPlugin(), getLocale());

	            if ( formError != null )
	            {
	                if ( formError.isMandatoryError(  ) )
	                {
	                    Object[] tabRequiredFields = { formError.getTitleQuestion(  ) };
	                    return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_QUESTION, tabRequiredFields,
	                    		AdminMessage.TYPE_STOP );
	                }
	                else
	                {
	                    Object[] tabFormError = { formError.getTitleQuestion(  ), formError.getErrorMessage(  ) };
	                    return AdminMessageService.getMessageUrl( request,  MESSAGE_FORM_ERROR, tabFormError, AdminMessage.TYPE_STOP );
	                }
	             }
	            // perform digg submit
	            
	            if ( nIdCategory != DiggUtils.CONSTANT_ID_NULL )
	            {
	                Category category = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin() );
	                diggSubmit.setCategory( category );
	            }

	            if ( nIdType != DiggUtils.CONSTANT_ID_NULL )
	            {
	                DiggSubmitType type = DiggSubmitTypeHome.findByPrimaryKey( nIdType, getPlugin());
	                diggSubmit.setDiggSubmitType( type );
	            }
	            
	            diggSubmit.setDisableComment( strDisableComment != null);
	            diggSubmit.setDisableVote( strDisableVote != null );
	            diggSubmit.setPinned( strEnablePin !=null );
	            _diggSubmitService.create( diggSubmit, getPlugin(),getLocale() );
	          
	            
	            
	            
	        }

	        return getJspManageDiggSubmit(request);
	    }
	    
	    
	    
	    
	    
	   



    /**
     * Return management CommentSubmit( list of comment submit)
     *
     * @param request
     *            The Http request
     * @return Html comment submit
     */
    public String getManageCommentSubmit( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        Locale locale = getLocale(  );

        String strIdCommentSort = request.getParameter( PARAMETER_ID_COMMENT_SORT );

        if ( ( strIdCommentSort != null ) && !strIdCommentSort.equals( EMPTY_STRING ) )
        {
            _nIdCommentSort = DiggUtils.getIntegerParameter( strIdCommentSort );
        }

        int nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_DIGG_SUBMIT_VALUE_SHOWN_CHARACTERS,
                100 );
        String strIdDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT );
        _strCurrentPageIndexCommentSubmit = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexCommentSubmit );
        _nItemsPerPageCommentSubmit = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageCommentSubmit, _nDefaultItemsPerPage );

        if ( ( strIdDiggSubmit != null ) && !strIdDiggSubmit.equals( EMPTY_STRING ) )
        {
            _nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmit );
        }

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( _nIdDiggSubmit, false, plugin );
        Digg digg = DiggHome.findByPrimaryKey( diggSubmit.getDigg(  ).getIdDigg(  ), plugin );

        if ( ( diggSubmit == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
        {
        	throw new AccessDeniedException(  );
        }
        //add repoted Message
        if(diggSubmit.isReported())
        {
        	diggSubmit.setReportedMessages(ReportedMessageHome.getReportedMessageByDiggSubmit(diggSubmit.getIdDiggSubmit(), getPlugin()));
        }

        // build Filter
        SubmitFilter filter = DiggUtils.getDiggSubmitFilter( getSearchFields() );

        SubmitFilter commentFilter = new SubmitFilter(  );
        commentFilter.setIdDiggSubmit( _nIdDiggSubmit );
        commentFilter.setIdDigg( _nIdDigg );
        commentFilter.setIdDiggSubmitState( getSearchFields().getIdDiggSumitState() );
        commentFilter.setIdReported( getSearchFields().getIdDiggSubmitReport());
        DiggUtils.initCommentFilterBySort( commentFilter, _nIdCommentSort );

        List<CommentSubmit> listCommentSubmit = _commentSubmitService.getCommentSubmitList( commentFilter, getPlugin(  ) );

        ReferenceList refListCommentSort = DiggUtils.getRefListCommentSort( locale );

        ReferenceList refCategoryList = DiggUtils.getRefListCategory( digg.getCategories(  ) );
        DiggUtils.addEmptyItem(refCategoryList);
        
        HashMap model = new HashMap(  );
        Paginator paginator = new Paginator( listCommentSubmit, _nItemsPerPageCommentSubmit,
                getJspManageCommentSubmit( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexCommentSubmit );

        model.put( MARK_COMMENT_SORT_SELECTED, _nIdCommentSort );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageCommentSubmit );
        model.put( MARK_COMMENT_SUBMIT_LIST, paginator.getPageItems(  ) );
        model.put( MARK_DIGG_SUBMIT, diggSubmit );
        model.put( MARK_ID_DIGG_SUBMIT_PREV,
            _diggSubmitService.findPrevIdDiggSubmitInTheList( _nIdDiggSubmit, filter, plugin ) );
        model.put( MARK_ID_DIGG_SUBMIT_NEXT,
            _diggSubmitService.findNextIdDiggSubmitInTheList( _nIdDiggSubmit, filter, plugin ) );
        model.put( MARK_DISABLE_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_DISABLE );
        model.put( MARK_PUBLISH_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_PUBLISH );
        model.put( MARK_WAITING_FOR_PUBLISH_DIGG_SUBMIT_STATE_NUMBER, DiggSubmit.STATE_WAITING_FOR_PUBLISH );
        model.put( MARK_NUMBER_SHOWN_CHARACTERS, nNumberShownCharacters );
        model.put( MARK_CATEGORY_LIST, refCategoryList );
        model.put( MARK_LIST_COMMENT_SORT, refListCommentSort );
        model.put( MARK_DIGG, digg );

        setPageTitleProperty( PROPERTY_MANAGE_COMMENT_SUBMIT_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_COMMENT_SUBMIT, locale, model );

        // ReferenceList refMailingList;
        // refMailingList=AdminMailingListService.getMailingLists(adminUser);
        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Gets the confirmation page of delete digg submit
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete digg submit
     */
    public String getConfirmRemoveDiggSubmit( HttpServletRequest request )
    {
        String strIdDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        String strMessage;
        int nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmit );

        if ( nIdDiggSubmit == -1 )
        {
            return getHomeUrl( request );
        }

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdDiggSubmit, false, getPlugin(  ) );

        if ( ( diggSubmit == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        strMessage = MESSAGE_CONFIRM_REMOVE_DIGG_SUBMIT;

        UrlItem url = new UrlItem( JSP_DO_REMOVE_DIGG_SUBMIT );

        if ( strComment != null )
        {
            url.addParameter( strComment, strComment );
        }

        url.addParameter( PARAMETER_ID_DIGG_SUBMIT, nIdDiggSubmit );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the digg submit supression
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveDiggSubmit( HttpServletRequest request )
    {
        String strIdDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT );
        String strComment = request.getParameter( PARAMETER_COMMENT );
        Plugin plugin = getPlugin(  );
        int nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmit );

        if ( nIdDiggSubmit == -1 )
        {
            return getHomeUrl( request );
        }

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdDiggSubmit, false, getPlugin(  ) );

        if ( ( diggSubmit == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        String strUrlBack;

        if ( strComment != null )
        {
            // on r�cup�re l'url digg submit suivant
            strUrlBack = doFindNextDiggSubmit( request );
        }
        else
        {
            strUrlBack = getJspManageDiggSubmit( request );
        }

        _diggSubmitService.remove( nIdDiggSubmit, plugin );

        return strUrlBack;
    }

    /**
     * Perform the digg submit change state
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doChangeDiggSubmitState( HttpServletRequest request )
    {
        String strIdDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT );
        String strStateNumber = request.getParameter( PARAMETER_STATE_NUMBER );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        Plugin plugin = getPlugin(  );
        int nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmit );
        int nStateNumber = DiggUtils.getIntegerParameter( strStateNumber );

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdDiggSubmit, false, plugin );
        DiggSubmitState diggSubmitState = DiggSubmitStateHome.findByNumero( nStateNumber, plugin );

        if ( ( diggSubmit == null ) || ( diggSubmitState == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        diggSubmit.setDiggSubmitState( diggSubmitState );
        _diggSubmitService.update( diggSubmit, plugin );

        if ( strComment != null )
        {
            return getJspManageCommentSubmit( request );
        }

        return getJspManageDiggSubmit( request );
    }
    
    
    /**
     * Gets the confirmation page of delete directory record
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete directory record
     */
    public String getConfirmMassChangeDiggSubmitCategory( HttpServletRequest request )
        throws AccessDeniedException
    {
    	
    	 // fill the selected records
        String[] selectedDiggSubmit = request.getParameterValues( PARAMETER_SELECTED_DIGG_SUBMIT);
        int nIdDiggSubmit;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = DiggUtils.getIntegerParameter( strIdCategory );
        UrlItem url = new UrlItem( JSP_DO_CHANGE_DIGG_SUBMIT_CATEGORY );
        url.addParameter(PARAMETER_ID_CATEGORY, nIdCategory);
    	//test All ressource selected before update
    	for ( String strIdDiggSubmit : selectedDiggSubmit )
         {
    	
         	if ( StringUtils.isNotBlank( strIdDiggSubmit ) && StringUtils.isNumeric( strIdDiggSubmit ) )
             {
         		
         		 nIdDiggSubmit=DiggUtils.getIntegerParameter(strIdDiggSubmit);
             	 DiggSubmit diggSubmit = DiggSubmitService.getService().findByPrimaryKey( nIdDiggSubmit, false, getPlugin() );
                

                  if ( ( diggSubmit == null ) ||
                          !RBACService.isAuthorized( Digg.RESOURCE_TYPE, DiggUtils.EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                              DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT,getUser()) )
                  {
                      throw new AccessDeniedException();
                  }
                  url.addParameter( PARAMETER_SELECTED_DIGG_SUBMIT, nIdDiggSubmit );
                 
             }
         		
            }
    	
    		
    		if(nIdCategory != DiggUtils.CONSTANT_ID_NULL)
    		{
    			Category category = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin() ); 
    			Object[] args ={ (category!=null && category.getTitle() == null ) ? "" : category.getTitle() };
    			return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_CHANGE_DIGG_SUBMIT_CATEGORY,args, url.getUrl(  ),
    		                AdminMessage.TYPE_CONFIRMATION );
    		       
    		}
    		else
    		{
    			
    			return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DIGG_SUBMIT_CATEGORY, url.getUrl(  ),
		                AdminMessage.TYPE_CONFIRMATION );
	
    		}
           

         }

    /**
     * Perform the digg submit change state
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException 
     */
    public String doMassChangeDiggSubmitCategory( HttpServletRequest request ) throws AccessDeniedException
    {
    	 // fill the selected records
        String[] selectedDiggSubmit = request.getParameterValues( PARAMETER_SELECTED_DIGG_SUBMIT);
        int nIdDiggSubmit;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = DiggUtils.getIntegerParameter( strIdCategory );
        Category categorySelected=null;
       	//test All ressource selected before update
    	for ( String strIdDiggSubmit : selectedDiggSubmit )
         {
    		
         	if ( StringUtils.isNotBlank( strIdDiggSubmit ) && StringUtils.isNumeric( strIdDiggSubmit ) )
             {
         		
         		 nIdDiggSubmit=DiggUtils.getIntegerParameter(strIdDiggSubmit);
             	 DiggSubmit diggSubmit = DiggSubmitService.getService().findByPrimaryKey( nIdDiggSubmit, false, getPlugin() );
                

                  if ( ( diggSubmit == null ) ||
                          !RBACService.isAuthorized( Digg.RESOURCE_TYPE, DiggUtils.EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                              DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT,getUser()) )
                  {
                      throw new AccessDeniedException();
                  }

                 
             }
         	
            }
    	
  
    	if(nIdCategory!=DiggUtils.CONSTANT_ID_NULL)
    	{
    		categorySelected = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin() ); 
    	}
      	//update all digg submit selected
    	for ( String strIdDiggSubmittoUpdate : selectedDiggSubmit )
        {
    		if ( StringUtils.isNotBlank( strIdDiggSubmittoUpdate ) && StringUtils.isNumeric( strIdDiggSubmittoUpdate ) )
            {
    			 nIdDiggSubmit=DiggUtils.getIntegerParameter(strIdDiggSubmittoUpdate);
	    		 DiggSubmit diggSubmit = DiggSubmitService.getService().findByPrimaryKey( nIdDiggSubmit, false, getPlugin() );
	        	 diggSubmit.setCategory( categorySelected );
	        	 DiggSubmitService.getService().update( diggSubmit, getPlugin() );
            }
        }
    	
    	
         
        return getJspManageDiggSubmit( request );
    }
    
    
    /**
     * Perform the digg submit change state
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doChangeDiggSubmitCategory( HttpServletRequest request )
    {
        String strIdDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        Plugin plugin = getPlugin(  );
        int nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmit );
        int nIdCategory = DiggUtils.getIntegerParameter( strIdCategory );

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdDiggSubmit, false, plugin );
        Category category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );

        if ( ( diggSubmit == null ) || ( category == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        diggSubmit.setCategory( category );

        _diggSubmitService.update( diggSubmit, plugin );

        if ( strComment != null )
        {
            return getJspManageCommentSubmit( request );
        }

        return getJspManageDiggSubmit( request );
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
        if ( request.getParameter( PARAMETER_DELETE ) != null )
        {
            String strIdCommentSubmit = request.getParameter( PARAMETER_ID_COMMENT_SUBMIT );
            String strMessage;
            int nIdCommentSubmit = DiggUtils.getIntegerParameter( strIdCommentSubmit );

            if ( nIdCommentSubmit == -1 )
            {
                return getHomeUrl( request );
            }

            strMessage = MESSAGE_CONFIRM_REMOVE_COMMENT_SUBMIT;

            UrlItem url = new UrlItem( JSP_DO_REMOVE_COMMENT_SUBMIT );
            url.addParameter( PARAMETER_ID_COMMENT_SUBMIT, nIdCommentSubmit );

            return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION );
        }
        else if ( request.getParameter( PARAMETER_ENABLE ) != null )
        {
            return doEnableCommentSubmit( request );
        }
        else if ( request.getParameter( PARAMETER_DISABLE ) != null )
        {
            return doDisableCommentSubmit( request );
        }
        else
        {
            return getJspManageCommentSubmit( request );
        }
    }

    /**
     * Submit an official answer to a diggSubmit
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doSubmitOfficialAnswer( HttpServletRequest request )
    {
        String strCommentValueDigg = request.getParameter( PARAMETER_COMMENT_VALUE );
        int nIdSubmitDigg = Integer.valueOf( request.getParameter( PARAMETER_ID_DIGG_SUBMIT ) );
        CommentSubmit commentSubmit = new CommentSubmit(  );
        Plugin plugin = getPlugin(  );

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdSubmitDigg, false, plugin );

        // Check XSS characters
        if ( StringUtil.containsXssCharacters( strCommentValueDigg ) )
        {
            strCommentValueDigg = "";

            return AdminMessageService.getMessageUrl( request, MESSAGE_NEW_COMMENT_SUBMIT_INVALID, SiteMessage.TYPE_STOP );
        }

        commentSubmit.setActive( true );
        diggSubmit.setNumberCommentEnable( diggSubmit.getNumberCommentEnable(  ) + 1 );
        _diggSubmitService.update( diggSubmit, plugin );

        commentSubmit.setDateComment( DiggUtils.getCurrentDate(  ) );
        commentSubmit.setDiggSubmit( diggSubmit );
        commentSubmit.setValue( strCommentValueDigg );
        commentSubmit.setOfficialAnswer( true );

        LuteceUser user = null;

        try
        {
            user = SecurityService.getInstance(  ).getRemoteUser( request );
        }
        catch ( Exception e )
        {
            AppLogService.error( "User not identified" );
        }

        if ( user != null )
        {
            commentSubmit.setLuteceUserKey( user.getName(  ) );
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
        Plugin plugin = getPlugin(  );
        int nIdCommentSubmit = DiggUtils.getIntegerParameter( strIdCommentSubmit );
        CommentSubmit commentSubmit = _commentSubmitService.findByPrimaryKey( nIdCommentSubmit, plugin );

        if ( commentSubmit == null )
        {
            return getJspManageDigg( request );
        }

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( commentSubmit.getDiggSubmit(  ).getIdDiggSubmit(  ),
                false, plugin );

        if ( !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        _commentSubmitService.remove( nIdCommentSubmit, plugin );

        if ( commentSubmit.isActive(  ) )
        {
            diggSubmit.setNumberCommentEnable( diggSubmit.getNumberCommentEnable(  ) - 1 );
        }

        _diggSubmitService.update( diggSubmit, plugin );

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
        Plugin plugin = getPlugin(  );
        int nIdCommentSubmit = DiggUtils.getIntegerParameter( strIdCommentSubmit );
        CommentSubmit commentSubmit = _commentSubmitService.findByPrimaryKey( nIdCommentSubmit, plugin );

        if ( commentSubmit != null )
        {
            DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( commentSubmit.getDiggSubmit(  )
                                                                                      .getIdDiggSubmit(  ), false,
                    plugin );

            if ( !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                        DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
            {
                return getJspManageDigg( request );
            }

            commentSubmit.setActive( false );
            _commentSubmitService.update( commentSubmit, plugin );
            diggSubmit.setNumberCommentEnable( diggSubmit.getNumberCommentEnable(  ) - 1 );
            _diggSubmitService.update( diggSubmit, plugin );
        }

        return getJspManageCommentSubmit( request );
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
        Plugin plugin = getPlugin(  );
        int nIdCommentSubmit = DiggUtils.getIntegerParameter( strIdCommentSubmit );
        CommentSubmit commentSubmit = _commentSubmitService.findByPrimaryKey( nIdCommentSubmit, plugin );

        if ( commentSubmit != null )
        {
            DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( commentSubmit.getDiggSubmit(  )
                                                                                      .getIdDiggSubmit(  ), false,
                    plugin );

            if ( !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                        DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, getUser(  ) ) )
            {
                return getJspManageDigg( request );
            }

            commentSubmit.setActive( true );
            _commentSubmitService.update( commentSubmit, plugin );
            diggSubmit.setNumberCommentEnable( diggSubmit.getNumberCommentEnable(  ) + 1 );
            _diggSubmitService.update( diggSubmit, plugin );
        }

        return getJspManageCommentSubmit( request );
    }

    /**
     * return the url of the next digg submit
     *
     * @param request
     *            The HTTP request
     * @return return the url of the next digg submit
     */
    public String doFindNextDiggSubmit( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        SubmitFilter filter = DiggUtils.getDiggSubmitFilter( getSearchFields() );
         _nIdDiggSubmit = _diggSubmitService.findNextIdDiggSubmitInTheList( _nIdDiggSubmit, filter, plugin );

        return getJspManageCommentSubmit( request );
    }

    /**
     * return the url of the prev digg submit
     *
     * @param request
     *            The HTTP request
     * @return return the url of the next digg submit
     */
    public String doFindPrevDiggSubmit( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        SubmitFilter filter = DiggUtils.getDiggSubmitFilter( getSearchFields() );
        _nIdDiggSubmit = _diggSubmitService.findPrevIdDiggSubmitInTheList( _nIdDiggSubmit, filter, plugin );

        return getJspManageCommentSubmit( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the digg
     * specified in parameter. return null if there is no error or else return
     * the error page url
     *
     * @param request
     *            the request
     * @param digg
     *            digg
     *
     * @return null if there is no error or else return the error page url
     */
    private String getDiggData( HttpServletRequest request, Digg digg )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strLibelleContribution = request.getParameter( PARAMETER_LIBELLE_CONTRIBUTION );
        String strUnavailabilityMessage = request.getParameter( PARAMETER_UNAVAILABILITY_MESSAGE );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strIdVoteType = request.getParameter( PARAMETER_ID_VOTE_TYPE );
        String strActivePropositionState = request.getParameter( PARAMETER_ACTIVE_PROPOSITION_STATE );
        String strNumberVoteRequired = request.getParameter( PARAMETER_NUMBER_VOTE_REQUIRED );
        String strNumberDayRequired = request.getParameter( PARAMETER_NUMBER_DAY_REQUIRED );
        String strNumberDiggSubmitInTopScore = request.getParameter( PARAMETER_NUMBER_DIGG_SUBMIT_IN_TOP_SCORE );
        String strNumberDiggSubmitCaractersShown = request.getParameter( PARAMETER_NUMBER_DIGG_SUBMIT_CARACTERS_SHOWN );
        String strNumberDiggSubmitInTopComment = request.getParameter( PARAMETER_NUMBER_DIGG_SUBMIT_IN_TOP_COMMENT );
        String strThemeXpage = request.getParameter( PARAMETER_THEME_XPAGE );

        String strActiveDiggSubmitAuthentification = request.getParameter( PARAMETER_ACTIVE_DIGG_SUBMIT_AUTHENTIFICATION );
        String strActiveVoteAuthentification = request.getParameter( PARAMETER_ACTIVE_VOTE_AUTHENTIFICATION );
        String strActiveCommentAuthentification = request.getParameter( PARAMETER_ACTIVE_COMMENT_AUTHENTIFICATION );

        String strDisableNewDiggSubmit = request.getParameter( PARAMETER_DISABLE_NEW_DIGG_SUBMIT );
        String strEnableMailNewDiggSubmit = request.getParameter( PARAMETER_ENABLE_MAIL_NEW_DIGG_SUBMIT );
        String strIdMailingListDiggSubmit = request.getParameter( PARAMETER_ID_MAILING_LIST_DIGG_SUBMIT );
       
        String strAuthorizedComment = request.getParameter( PARAMETER_AUTHORIZED_COMMENT );
        String strActiveEditorBbcode = request.getParameter( PARAMETER_ACTIVE_EDITOR_BBCODE_ON_COMMENT );
        String strDisableNewComment = request.getParameter( PARAMETER_DISABLE_NEW_COMMENT );
        String strLimitNumberVote = request.getParameter( PARAMETER_LIMIT_NUMBER_VOTE );
        String strActiveCaptcha = request.getParameter( PARAMETER_ACTIVE_CAPTCHA );
        String strLibelleValidateButton = request.getParameter( PARAMETER_LIBELLE_VALIDATE_BUTTON );
        String strShowCategoryBlock = request.getParameter( PARAMETER_SHOW_CATEGORY_BLOCK );
        String strShowTopScoreBlock = request.getParameter( PARAMETER_SHOW_TOP_SCORE_BLOCK );
        String strShowTopCommentBlock = request.getParameter( PARAMETER_SHOW_TOP_COMMENT_BLOCK );
        String strActiveDiggSubmitPaginator = request.getParameter( PARAMETER_ACTIVE_DIGG_SUBMIT_PAGINATOR );
        String strNumberDiggSubmitPerPage = request.getParameter( PARAMETER_NUMBER_DIGG_SUBMIT_PER_PAGE );
        String strRole = request.getParameter( PARAMETER_ROLE );
        String strHeader = request.getParameter( PARAMETER_HEADER );
        String strConfirmationMessage = request.getParameter( PARAMETER_CONFIRMATION_MESSAGE );
        String strIdDefaultSort = request.getParameter( PARAMETER_ID_DEFAULT_SORT );
        String strDisableVote= request.getParameter( PARAMETER_DISABLE_VOTE );
        String strDisplayCommentInDiggSubmitList= request.getParameter( PARAMETER_DISPLAY_COMMENT_IN_DIGG_SUBMIT_LIST );
        String strNumberCommentDisplayInDiggSubmitList= request.getParameter( PARAMETER_NUMBER_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST );
        String strNumberCharCommentDisplayInDiggSubmitList= request.getParameter( PARAMETER_NUMBER_CHAR_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST );
        String strEnableMailNewCommentSubmit = request.getParameter( PARAMETER_ENABLE_MAIL_NEW_COMMENT_SUBMIT );
        String strEnableMailNewReportedSubmit = request.getParameter( PARAMETER_ENABLE_MAIL_NEW_REPORTED_SUBMIT );
        
        int nIdVoteType = DiggUtils.getIntegerParameter( strIdVoteType );
        int nIdMailingListDiggSubmit = DiggUtils.getIntegerParameter( strIdMailingListDiggSubmit );
        int nNumberDayRequired = DiggUtils.getIntegerParameter( strNumberDayRequired );
        int nNumberVoteRequired = DiggUtils.getIntegerParameter( strNumberVoteRequired );
        int nNumberDiggSubmitInTopScore = DiggUtils.getIntegerParameter( strNumberDiggSubmitInTopScore );
        int nNumberDiggSubmitCaractersShown = DiggUtils.getIntegerParameter( strNumberDiggSubmitCaractersShown );
        int nNumberDiggSubmitInTopComment = DiggUtils.getIntegerParameter( strNumberDiggSubmitInTopComment );
        int nNumberDiggSubmitPerPage = DiggUtils.getIntegerParameter( strNumberDiggSubmitPerPage );
        int nIdDefaultSort = DiggUtils.getIntegerParameter( strIdDefaultSort );
        int nNumberCommentDisplayInDiggSubmitList = DiggUtils.getIntegerParameter( strNumberCommentDisplayInDiggSubmitList );
        int nNumberCharCommentDisplayInDiggSubmitList = DiggUtils.getIntegerParameter( strNumberCharCommentDisplayInDiggSubmitList );
        
        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else if ( ( strLibelleContribution == null ) || strLibelleContribution.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_LIBELLE_CONTRIBUTION;
        }

        else if ( ( strUnavailabilityMessage == null ) || strUnavailabilityMessage.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_UNAVAILABILITY_MESSAGE;
        }
        else if ( nIdVoteType == -1 )
        {
            strFieldError = FIELD_VOTE_TYPE;
        }
        else if ( ( strNumberDiggSubmitCaractersShown == null ) ||
                strNumberDiggSubmitCaractersShown.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_NUMBER_DIGG_SUBMIT_CARACTERS_SHOWN;
        }
        else if ( ( ( ( strShowTopScoreBlock != null ) && strShowTopScoreBlock.trim(  ).equals( CONSTANTE_YES_VALUE ) ) &&
                ( strNumberDiggSubmitInTopScore == null ) ) ||
                strNumberDiggSubmitInTopScore.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_NUMBER_DIGG_SUBMIT_IN_TOP_SCORE;
        }

        else if ( ( ( strAuthorizedComment != null ) && strAuthorizedComment.trim(  ).equals( CONSTANTE_YES_VALUE ) ) &&
                ( ( strNumberDiggSubmitInTopComment == null ) ||
                strNumberDiggSubmitInTopComment.trim(  ).equals( EMPTY_STRING ) ) )
        {
            strFieldError = FIELD_NUMBER_DIGG_SUBMIT_IN_TOP_COMMENT;
        }

        else if ( ( ( strActiveDiggSubmitPaginator != null ) &&
                strActiveDiggSubmitPaginator.trim(  ).equals( CONSTANTE_YES_VALUE ) ) &&
                ( ( strNumberDiggSubmitPerPage == null ) || strNumberDiggSubmitPerPage.trim(  ).equals( EMPTY_STRING ) ) )
        {
            strFieldError = FIELD_NUMBER_DIGG_SUBMIT_PER_PAGE;
        }

        else if ( ( strLibelleValidateButton == null ) || strLibelleValidateButton.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_LIBELE_VALIDATE_BUTTON;
        }
        else if ( ( ( strDisplayCommentInDiggSubmitList != null ) &&
                ( ( strNumberCommentDisplayInDiggSubmitList == null ) || strNumberCommentDisplayInDiggSubmitList.trim(  ).equals( EMPTY_STRING )  )))
        {
            strFieldError = FIELD_NUMBER_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST;
        }
        else if ( ( ( strDisplayCommentInDiggSubmitList != null ) &&
                ( ( strNumberCharCommentDisplayInDiggSubmitList == null ) || strNumberCharCommentDisplayInDiggSubmitList.trim(  ).equals( EMPTY_STRING )  )))
        {
            strFieldError = FIELD_NUMBER_CHAR_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( nNumberDiggSubmitCaractersShown < 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_CARACTERS_SHOWN,
                AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberDiggSubmitInTopScore != null ) &&
                !strNumberDiggSubmitInTopScore.trim(  ).equals( EMPTY_STRING ) && ( nNumberDiggSubmitInTopScore < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_IN_TOP_SCORE,
                AdminMessage.TYPE_STOP );
        }

        if ( ( strActivePropositionState != null ) && strActivePropositionState.trim(  ).equals( CONSTANTE_YES_VALUE ) &&
                ( strNumberVoteRequired != null ) && !strNumberVoteRequired.trim(  ).equals( EMPTY_STRING ) &&
                ( nNumberVoteRequired < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_VOTE_REQUIRED,
                AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberDayRequired != null ) && !strNumberDayRequired.trim(  ).equals( EMPTY_STRING ) &&
                ( nNumberDayRequired < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_DAY_REQUIRED,
                AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberDiggSubmitInTopComment != null ) &&
                !strNumberDiggSubmitInTopComment.trim(  ).equals( EMPTY_STRING ) &&
                ( nNumberDiggSubmitInTopComment < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_IN_TOP_COMMENT,
                AdminMessage.TYPE_STOP );
        }

        if ( ( strNumberDiggSubmitPerPage != null ) && !strNumberDiggSubmitPerPage.trim(  ).equals( EMPTY_STRING ) &&
                ( nNumberDiggSubmitPerPage < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_DIGG_SUBMIT_PER_PAGE,
                AdminMessage.TYPE_STOP );
        }
        
        if ( (strDisplayCommentInDiggSubmitList!=null ) && ( strNumberCommentDisplayInDiggSubmitList != null ) && !strNumberCommentDisplayInDiggSubmitList.trim(  ).equals( EMPTY_STRING ) &&
                ( nNumberCommentDisplayInDiggSubmitList < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST,
                AdminMessage.TYPE_STOP );
        }
        if ( (strDisplayCommentInDiggSubmitList!=null ) && ( strNumberCharCommentDisplayInDiggSubmitList != null ) && !strNumberCharCommentDisplayInDiggSubmitList.trim(  ).equals( EMPTY_STRING ) &&
                ( nNumberCommentDisplayInDiggSubmitList < 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_CHAR_COMMENT_DISPLAY_IN_DIGG_SUBMIT_LIST,
                AdminMessage.TYPE_STOP );
        }
        
        

        digg.setTitle( strTitle );
        digg.setLibelleContribution( strLibelleContribution );
        digg.setUnavailabilityMessage( strUnavailabilityMessage );
        digg.setWorkgroup( strWorkgroup );

        if ( digg.getVoteType(  ) == null )
        {
            digg.setVoteType( new VoteType(  ) );
        }

        digg.getVoteType(  ).setIdVoteType( nIdVoteType );

        if ( ( strActivePropositionState != null ) && strActivePropositionState.trim(  ).equals( CONSTANTE_YES_VALUE ) )
        {
            digg.setActiveDiggPropositionState( true );
            digg.setNumberVoteRequired( nNumberVoteRequired );
        }
        else
        {
            digg.setActiveDiggPropositionState( false );
            digg.setNumberVoteRequired( -1 );
        }

        digg.setNumberDayRequired( nNumberDayRequired );
        digg.setActiveDiggSubmitAuthentification( strActiveDiggSubmitAuthentification != null );
        digg.setActiveVoteAuthentification( strActiveVoteAuthentification != null );
        digg.setActiveCommentAuthentification( strActiveCommentAuthentification != null );
        digg.setActiveCaptcha( strActiveCaptcha != null );
        digg.setDisableNewDiggSubmit( strDisableNewDiggSubmit != null );
        digg.setEnableMailNewDiggSubmit( strEnableMailNewDiggSubmit!=null );
        digg.setEnableMailNewCommentSubmit( strEnableMailNewCommentSubmit!=null );
        digg.setEnableMailNewReportedSubmit( strEnableMailNewReportedSubmit!=null );
        digg.setIdMailingListDiggSubmit(nIdMailingListDiggSubmit);
        digg.setAuthorizedComment(strAuthorizedComment != null );
        digg.setDisableNewComment( strDisableNewComment !=null );
        digg.setActiveEditorBbcode(strActiveEditorBbcode != null  );
        digg.setLimitNumberVote( strLimitNumberVote != null  );
        digg.setShowCategoryBlock( strShowCategoryBlock != null );
        digg.setShowTopScoreBlock( strShowTopScoreBlock != null );
        digg.setShowTopCommentBlock( strShowTopCommentBlock != null );
        digg.setActiveDiggSubmitPaginator( strActiveDiggSubmitPaginator != null );

        if ( strThemeXpage != null )
        {
            digg.setCodeTheme( strThemeXpage );
        }
        
        

        digg.setLibelleValidateButton( strLibelleValidateButton );
        digg.setNumberDiggSubmitInTopScore( nNumberDiggSubmitInTopScore );
        digg.setNumberDiggSubmitInTopComment( nNumberDiggSubmitInTopComment );
        digg.setNumberDiggSubmitCaractersShown( nNumberDiggSubmitCaractersShown );
        digg.setNumberDiggSubmitPerPage( nNumberDiggSubmitPerPage );
        digg.setRole( strRole );
        digg.setHeader( strHeader );
        digg.setConfirmationMessage( strConfirmationMessage );
        digg.setIdDefaultSort( nIdDefaultSort );
        digg.setDisableVote( strDisableVote != null );
        digg.setDisplayCommentInDiggSubmitList(strDisplayCommentInDiggSubmitList !=null);
        digg.setNumberCommentDisplayInDiggSubmitList(nNumberCommentDisplayInDiggSubmitList);
        digg.setNumberCharCommentDisplayInDiggSubmitList(nNumberCharCommentDisplayInDiggSubmitList);
        return null; // No error
    }

    /**
     * Gets the digg creation page
     *
     * @param request
     *            The HTTP request
     * @return The digg creation page
     */
    public String getCreateDigg( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Digg.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DigglikeResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return getManageDigg( request );
        }

        Plugin plugin = getPlugin(  );
        AdminUser adminUser = getUser(  );
        Locale locale = getLocale(  );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;
        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        refMailingList = new ReferenceList(  );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        // Style management
        String defaultTheme = ThemeHome.getGlobalTheme(  );
        Collection<Theme> themes = ThemeHome.getThemesList(  );
        ReferenceList themesRefList = new ReferenceList(  );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme(  ), theme.getThemeDescription(  ) );
        }

        ReferenceList refListDiggSort = DiggUtils.getRefListDiggSort( locale );
        ReferenceList refVoteTypeList = initRefListVoteType( plugin, locale );
        DefaultMessage defaultMessage = DefaultMessageHome.find( plugin );
        HashMap model = new HashMap(  );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_DEFAULT_MESSAGE, defaultMessage );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_YES_VALUE, CONSTANTE_YES_VALUE );
        model.put( MARK_NO_VALUE, CONSTANTE_NO_VALUE );
        model.put( MARK_VOTE_TYPE_LIST, refVoteTypeList );
        model.put( MARK_AUTHENTIFICATION_ENABLE, SecurityService.isAuthenticationEnable(  ) );
        model.put( MARK_ROLE_LIST, RoleHome.getRolesList(  ) );
        model.put( MARK_DEFAULT_VALUE_ROLE, Digg.ROLE_NONE );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_DEFAULT_THEME, defaultTheme );
        model.put( MARK_LIST_DIGG_SUBMIT_SORT, refListDiggSort );

        setPageTitleProperty( PROPERTY_CREATE_DIGG_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DIGG, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the digg creation
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateDigg( HttpServletRequest request )
    {
        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DigglikeResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            Plugin plugin = getPlugin(  );
            Digg digg = new Digg(  );
            String strError = getDiggData( request, digg );

            if ( strError != null )
            {
                return strError;
            }

            digg.setIdDigg( DiggHome.create( digg, plugin ) );

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyDigg( request, digg.getIdDigg(  ) );
            }

            Theme theme = ThemeHome.findByPrimaryKey( digg.getCodeTheme(  ) );
            ( (DigglikePlugin) getPlugin(  ) ).addXPageTheme( digg.getIdDigg(  ), theme );
        }

        return getJspManageDigg( request );
    }

    /**
     * Gets the digg modification page
     *
     * @param request
     *            The HTTP request
     * @return The digg modification page
     */
    public String getModifyDigg( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        List<IEntry> listEntry;
        int nNumberQuestion;
        EntryFilter filter;
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = -1;
        int nIdEntryFistInTheList = -1;
        int nIdEntryLastInTheList = -1;
        Digg digg = null;

        if ( ( strIdDigg != null ) && !strIdDigg.equals( EMPTY_STRING ) )
        {
            nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
            digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );
        }

        if ( ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageDigg( request );
        }

        // initialisation de la variable de session
        _nIdDigg = nIdDigg;
        filter = new EntryFilter(  );
        filter.setIdDigg( digg.getIdDigg(  ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        if ( listEntry.size(  ) > 0 )
        {
            nIdEntryFistInTheList = listEntry.get( 0 ).getIdEntry(  );
            nIdEntryLastInTheList = listEntry.get( listEntry.size(  ) - 1 ).getIdEntry(  );
        }

        nNumberQuestion = EntryHome.getNumberEntryByFilter( filter, plugin );

        _strCurrentPageIndexEntry = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexEntry );
        _nItemsPerPageEntry = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageEntry, _nDefaultItemsPerPage );

        Paginator paginator = new Paginator( listEntry, _nItemsPerPageEntry,
                AppPathService.getBaseUrl( request ) + JSP_MODIFY_DIGG + "?id_digg=" + digg.getIdDigg(  ),
                PARAMETER_PAGE_INDEX, _strCurrentPageIndexEntry );

        AdminUser adminUser = getUser(  );

        Locale locale = getLocale(  );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;
        ReferenceList refEntryType;

        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );

        refMailingList = new ReferenceList(  );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        List<Category> listCategoriesView = CategoryHome.getList( plugin );
        List<DiggSubmitType> listDiggSubmitTypeView = DiggSubmitTypeHome.getList( plugin );
        
        listCategoriesView.removeAll( digg.getCategories(  ) );
        listDiggSubmitTypeView.removeAll(digg.getDiggSubmitTypes());
        ReferenceList refCategoryList = DiggUtils.getRefListCategory( listCategoriesView );
        ReferenceList refVoteTypeList = initRefListVoteType( plugin, locale );
        ReferenceList refListDiggSort = DiggUtils.getRefListDiggSort( locale );
        ReferenceList refListDiggSubmitType=DiggUtils.getRefListType(listDiggSubmitTypeView);

        EntryType entryTypeGroup = new EntryType(  );
        refEntryType = initRefListEntryType( plugin, locale );

        // Style management
        Collection<Theme> themes = ThemeHome.getThemesList(  );
        ReferenceList themesRefList = new ReferenceList(  );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme(  ), theme.getThemeDescription(  ) );
        }

        if ( digg.getCodeTheme(  ) == null )
        {
            digg.setCodeTheme( ThemeHome.getGlobalTheme(  ) );
        }

        HashMap model = new HashMap(  );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageEntry );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_ENTRY_TYPE_REF_LIST, refEntryType );
        model.put( MARK_ENTRY_TYPE_GROUP, entryTypeGroup );
        model.put( MARK_DIGG, digg );
        model.put( MARK_ENTRY_LIST, paginator.getPageItems(  ) );
        model.put( MARK_NUMBER_QUESTION, nNumberQuestion );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_YES_VALUE, CONSTANTE_YES_VALUE );
        model.put( MARK_NO_VALUE, CONSTANTE_NO_VALUE );
        model.put( MARK_CATEGORY_LIST, refCategoryList );
        model.put( MARK_VOTE_TYPE_LIST, refVoteTypeList );
        model.put( MARK_DIGG_SUBMIT_TYPE_LIST, refListDiggSubmitType);
        model.put( MARK_ID_ENTRY_FIRST_IN_THE_LIST, nIdEntryFistInTheList );
        model.put( MARK_ID_ENTRY_LAST_IN_THE_LIST, nIdEntryLastInTheList );
        model.put( MARK_AUTHENTIFICATION_ENABLE, SecurityService.isAuthenticationEnable(  ) );
        model.put( MARK_ROLE_LIST, RoleHome.getRolesList(  ) );
        model.put( MARK_DEFAULT_VALUE_ROLE, Digg.ROLE_NONE );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_LIST_DIGG_SUBMIT_SORT, refListDiggSort );

        setPageTitleProperty( PROPERTY_MODIFY_DIGG_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DIGG, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the digg modification
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyDigg( HttpServletRequest request )
    {
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) && ( nIdDigg != -1 ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            Plugin plugin = getPlugin(  );
            Digg digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );
            String strOldTheme = digg.getCodeTheme(  );
            String strError = getDiggData( request, digg );

            if ( strError != null )
            {
                return strError;
            }

            DiggHome.update( digg, plugin );

            String strNewTheme = digg.getCodeTheme(  );

            if ( !strNewTheme.equals( strOldTheme ) )
            {
                Theme newTheme = ThemeHome.findByPrimaryKey( strNewTheme );
                ( (DigglikePlugin) getPlugin(  ) ).addXPageTheme( nIdDigg, newTheme );
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyDigg( request, digg.getIdDigg(  ) );
            }
        }

        return getJspManageDigg( request );
    }
    
    
    /**
     * Perform add digg submit type association
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doInsertDiggSubmitType( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Digg digg;
        String strIdDiggSubmitType = request.getParameter( PARAMETER_ID_DIGG_SUBMIT_TYPE );
        int nIDiggSubmitType = DiggUtils.getIntegerParameter( strIdDiggSubmitType );
        DiggSubmitType diggSubmitType = DiggSubmitTypeHome.findByPrimaryKey( nIDiggSubmitType, plugin );

        digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( diggSubmitType != null ) && ( digg != null ) && ( digg.getDiggSubmitTypes(  ) != null ) &&
                !digg.getDiggSubmitTypes().contains( diggSubmitType ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
        	DiggSubmitTypeHome.createDiggAssociation( _nIdDigg, nIDiggSubmitType, plugin );
        }

        if ( _nIdDigg != -1 )
        {
            return getJspModifyDigg( request, _nIdDigg );
        }

        return getJspManageDigg( request );
    }

    /**
     * remove DiggSubmitType association
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveDiggSubmitType( HttpServletRequest request )
    {
    	Plugin plugin = getPlugin(  );
        Digg digg;
        String strIdDiggSubmitType = request.getParameter( PARAMETER_ID_DIGG_SUBMIT_TYPE );
        int nIDiggSubmitType = DiggUtils.getIntegerParameter( strIdDiggSubmitType );
        DiggSubmitType diggSubmitType = DiggSubmitTypeHome.findByPrimaryKey( nIDiggSubmitType, plugin );

        digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( diggSubmitType != null ) && ( digg != null ) && 
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
        	DiggSubmitTypeHome.removeDiggAssociation( _nIdDigg, nIDiggSubmitType, plugin );
        }

        if ( _nIdDigg != -1 )
        {
            return getJspModifyDigg( request, _nIdDigg );
        }

        return getJspManageDigg( request );
    }
    

    /**
     * Perform add a category in the digg
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doInsertCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Category category;
        Digg digg;
        String strCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = DiggUtils.getIntegerParameter( strCategory );
        category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );

        digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( category != null ) && ( digg != null ) && ( digg.getCategories(  ) != null ) &&
                !digg.getCategories(  ).contains( category ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
        	CategoryHome.createDiggAssociation( _nIdDigg, nIdCategory, plugin );
        }

        if ( _nIdDigg != -1 )
        {
            return getJspModifyDigg( request, _nIdDigg );
        }

        return getJspManageDigg( request );
    }

    /**
     * Perform add a category in the digg
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Category category;
        Digg digg;
        String strCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = DiggUtils.getIntegerParameter( strCategory );
        category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );
        digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( category != null ) && ( digg != null ) && ( digg.getCategories(  ) != null ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
        	CategoryHome.removeDiggAssociation( _nIdDigg, nIdCategory, plugin );
        }

        if ( _nIdDigg != -1 )
        {
            return getJspModifyDigg( request, _nIdDigg );
        }

        return getJspManageDigg( request );
    }

    /**
     * Gets the confirmation page of delete digg
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete digg
     */
    public String getConfirmRemoveDigg( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        String strMessage;
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        if ( ( nIdDigg == -1 ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        SubmitFilter responseFilter = new SubmitFilter(  );
        responseFilter.setIdDigg( nIdDigg );

        int nNumberdiggSubmit = _diggSubmitService.getCountDiggSubmit( responseFilter, plugin );

        if ( nNumberdiggSubmit == 0 )
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_DIGG;
        }
        else
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_DIGG_WITH_DIGG_SUBMIT;
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_DIGG );
        url.addParameter( PARAMETER_ID_DIGG, strIdDigg );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the digg supression
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveDigg( HttpServletRequest request )
    {
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        Plugin plugin = getPlugin(  );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        if ( ( nIdDigg != -1 ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            DiggHome.remove( nIdDigg, plugin );
        }

        return getJspManageDigg( request );
    }

    /**
     * copy the digg whose key is specified in the Http request
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCopyDigg( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Digg digg;
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        if ( ( nIdDigg == -1 ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_COPY, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );

        Object[] tabDiggTileCopy = { digg.getTitle(  ) };
        String strTitleCopyDigg = I18nService.getLocalizedString( PROPERTY_COPY_DIGG_TITLE, tabDiggTileCopy,
                getLocale(  ) );

        if ( strTitleCopyDigg != null )
        {
            digg.setTitle( strTitleCopyDigg );
        }

        DiggHome.copy( digg, plugin );

        return getJspManageDigg( request );
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
        Plugin plugin = getPlugin(  );
        IEntry entry;
        Digg digg;

        entry = DiggUtils.createEntryByType( request, plugin );
        digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( entry == null ) || ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageDigg( request );
        }

        entry.setDigg( digg );

        HashMap model = new HashMap(  );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        setPageTitleProperty( PROPERTY_CREATE_QUESTION_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateCreate(  ), getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
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
        Plugin plugin = getPlugin(  );
        IEntry entry;

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            entry = DiggUtils.createEntryByType( request, plugin );

            Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

            if ( ( entry == null ) || ( digg == null ) ||
                    !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                        DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
            {
                return getJspManageDigg( request );
            }

            String strError = entry.getRequestData( request, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }

            entry.setDigg( digg );
            entry.setIdEntry( EntryHome.create( entry, plugin ) );

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyEntry( request, entry.getIdEntry(  ) );
            }
        }

        return getJspModifyDigg( request, _nIdDigg );
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
        Plugin plugin = getPlugin(  );
        IEntry entry;
        List<EntryAdditionalAttribute> entryAdditionalAttributeList = new ArrayList<EntryAdditionalAttribute>(  );
        ReferenceList refListRegularExpression;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );
        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );
        entryAdditionalAttributeList = EntryAdditionalAttributeHome.getList( nIdEntry, plugin );

        if ( ( entry == null ) || ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageDigg( request );
        }

        HashMap model = new HashMap(  );

        for ( EntryAdditionalAttribute entryAdditionalAttribute : entryAdditionalAttributeList )
        {
            model.put( entryAdditionalAttribute.getName(  ), entryAdditionalAttribute.getValue(  ) );
        }

        model.put( MARK_ENTRY, entry );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        Paginator paginator = entry.getPaginator( _nItemsPerPage,
                AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        if ( paginator != null )
        {
            model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
            model.put( MARK_NUMBER_ITEMS, paginator.getItemsCount(  ) );
            model.put( MARK_LIST, paginator.getPageItems(  ) );
            model.put( MARK_PAGINATOR, paginator );
        }

        refListRegularExpression = entry.getReferenceListRegularExpression( entry, plugin );

        if ( refListRegularExpression != null )
        {
            model.put( MARK_REGULAR_EXPRESSION_LIST_REF_LIST, refListRegularExpression );
        }

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        setPageTitleProperty( PROPERTY_MODIFY_QUESTION_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateModify(  ), getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
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
        Plugin plugin = getPlugin(  );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( entry == null ) || ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = entry.getRequestData( request, getLocale(  ) );

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

        return getJspModifyDigg( request, digg.getIdDigg(  ) );
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
        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );

        if ( nIdEntry == -1 )
        {
            return getHomeUrl( request );
        }

        strMessage = MESSAGE_CONFIRM_REMOVE_ENTRY;

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ENTRY );
        url.addParameter( PARAMETER_ID_ENTRY, strIdEntry + "#list" );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
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
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );
        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( nIdEntry == -1 ) || ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        EntryHome.remove( nIdEntry, plugin );

        return getJspModifyDigg( request, digg.getIdDigg(  ) );
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
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );
        IEntry entry;
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( entry == null ) || ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        Object[] tabEntryTileCopy = { entry.getTitle(  ) };
        String strTitleCopyEntry = I18nService.getLocalizedString( PROPERTY_COPY_ENTRY_TITLE, tabEntryTileCopy,
                getLocale(  ) );

        if ( strTitleCopyEntry != null )
        {
            entry.setTitle( strTitleCopyEntry );
        }

        EntryHome.copy( entry, plugin );

        return getJspModifyDigg( request, digg.getIdDigg(  ) );
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
        Plugin plugin = getPlugin(  );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( entry == null ) || ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDigg( entry.getDigg(  ).getIdDigg(  ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        int nIndexEntry = DiggUtils.getIndexEntryInTheEntryList( nIdEntry, listEntry );

        if ( nIndexEntry != 0 )
        {
            int nNewPosition;
            IEntry entryToInversePosition;
            entryToInversePosition = listEntry.get( nIndexEntry - 1 );
            entryToInversePosition = EntryHome.findByPrimaryKey( entryToInversePosition.getIdEntry(  ), plugin );
            nNewPosition = entryToInversePosition.getPosition(  );
            entryToInversePosition.setPosition( entry.getPosition(  ) );
            entry.setPosition( nNewPosition );
            EntryHome.update( entry, plugin );
            EntryHome.update( entryToInversePosition, plugin );
        }

        return getJspModifyDigg( request, digg.getIdDigg(  ) );
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
        Plugin plugin = getPlugin(  );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( entry == null ) || ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + _nIdDigg,
                    DigglikeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDigg( entry.getDigg(  ).getIdDigg(  ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        int nIndexEntry = DiggUtils.getIndexEntryInTheEntryList( nIdEntry, listEntry );

        if ( nIndexEntry != ( listEntry.size(  ) - 1 ) )
        {
            int nNewPosition;
            IEntry entryToInversePosition;
            entryToInversePosition = listEntry.get( nIndexEntry + 1 );
            entryToInversePosition = EntryHome.findByPrimaryKey( entryToInversePosition.getIdEntry(  ), plugin );
            nNewPosition = entryToInversePosition.getPosition(  );
            entryToInversePosition.setPosition( entry.getPosition(  ) );
            entry.setPosition( nNewPosition );
            EntryHome.update( entry, plugin );
            EntryHome.update( entryToInversePosition, plugin );
        }

        return getJspModifyDigg( request, digg.getIdDigg(  ) );
    }

    /**
     * Gets the confirmation page of disable digg
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of disable digg
     */
    public String getConfirmDisableDigg( HttpServletRequest request )
    {
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        if ( nIdDigg == -1 )
        {
            return getHomeUrl( request );
        }

        String strMessage;
        strMessage = MESSAGE_CONFIRM_DISABLE_DIGG;

        UrlItem url = new UrlItem( JSP_DO_DISABLE_DIGG );
        url.addParameter( PARAMETER_ID_DIGG, strIdDigg );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform disable digg
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doDisableDigg( HttpServletRequest request )
    {
        Digg digg;
        Plugin plugin = getPlugin(  );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );

        if ( ( digg != null ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            digg.setActive( false );
            DiggHome.update( digg, getPlugin(  ) );

            if ( digg.getDiggsSubmit(  ) != null )
            {
                for ( DiggSubmit submit : digg.getDiggsSubmit(  ) )
                {
                    String strIdDiggSubmit = Integer.toString( submit.getIdDiggSubmit(  ) );
                    IndexationService.addIndexerAction( strIdDiggSubmit + "_" + DigglikeIndexer.SHORT_NAME,
                        AppPropertiesService.getProperty( DigglikeIndexer.PROPERTY_INDEXER_NAME ),
                        IndexerAction.TASK_DELETE );

                    DiggIndexerUtils.addIndexerAction( strIdDiggSubmit, IndexerAction.TASK_DELETE );
                }
            }
        }

        return getJspManageDigg( request );
    }

    /**
     * Perform enable form
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doEnableDigg( HttpServletRequest request )
    {
        Digg digg;
        Plugin plugin = getPlugin(  );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );

        if ( ( digg != null ) &&
                RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            digg.setActive( true );
            DiggHome.update( digg, getPlugin(  ) );

            if ( digg.getDiggsSubmit(  ) != null )
            {
                for ( DiggSubmit submit : digg.getDiggsSubmit(  ) )
                {
                    String strIdDiggSubmit = Integer.toString( submit.getIdDiggSubmit(  ) );
                    IndexationService.addIndexerAction( strIdDiggSubmit,
                        AppPropertiesService.getProperty( DigglikeIndexer.PROPERTY_INDEXER_NAME ),
                        IndexerAction.TASK_CREATE );

                    DiggIndexerUtils.addIndexerAction( strIdDiggSubmit, IndexerAction.TASK_CREATE );
                }
            }
        }

        return getJspManageDigg( request );
    }

    /**
     * return url of the jsp manage digg
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage digg
     */
    private String getJspManageDigg( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIGG;
    }

    /**
     * return url of the jsp manage diggSubmitType
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage digg
     */
    private String getJspManageDiggSubmitType( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIGG_SUBMIT_TYPE;
    }

    /**
     * return url of the jsp manage digg submit
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage digg submit
     */
    public static String getJspManageDiggSubmit( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIGG_SUBMIT;
    }
    /**
     * return url of the jsp manage digg submit order
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage digg submit order
     */
    public static String getJspManageDiggSubmitOrder( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIGG_SUBMIT_ORDER;
    }
    

    /**
     * return url of the jsp manage comment submit
     *
     * @param request
     *            The HTTP request
     * @return url of the jsp manage comment submit
     */
    private String getJspManageCommentSubmit( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_COMMENT_SUBMIT;
    }

    /**
     * return url of the jsp modify digg
     *
     * @param request
     *            The HTTP request
     * @param nIdDigg
     *            the key of digg to modify
     * @return return url of the jsp modify digg
     */
    private String getJspModifyDigg( HttpServletRequest request, int nIdDigg )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_DIGG + "?id_digg=" + nIdDigg;
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
     * return url of the jsp manage advanced  parameters
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
        ReferenceList refListEntryType = new ReferenceList(  );
        List<EntryType> listEntryType = EntryTypeHome.getList( plugin );

        for ( EntryType entryType : listEntryType )
        {
            refListEntryType.addItem( entryType.getIdType(  ), entryType.getTitle(  ) );
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
        ReferenceList refListVoteType = new ReferenceList(  );
        List<VoteType> listVoteType = VoteTypeHome.getList( plugin );

        for ( VoteType voteType : listVoteType )
        {
            refListVoteType.addItem( voteType.getIdVoteType(  ), voteType.getTitle(  ) );
        }

        return refListVoteType;
    }

    /**
     * Init reference list whidth the different digg submit state
     *
     * @param plugin
     *            the plugin
     * @param locale
     *            the locale
     * @return reference the different digg submit state
     */
    private ReferenceList initRefListDiggSubmitState( Plugin plugin, Locale locale )
    {
        ReferenceList refListDiggSubmitState = new ReferenceList(  );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        List<DiggSubmitState> listDiggSubmitState = DiggSubmitStateHome.getList( plugin );
        refListDiggSubmitState.addItem( -1, strAll );

        for ( DiggSubmitState diggSubmitState : listDiggSubmitState )
        {
            refListDiggSubmitState.addItem( diggSubmitState.getIdDiggSubmitState(  ), diggSubmitState.getTitle(  ) );
        }

        return refListDiggSubmitState;
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

        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );
        int nIdExpression = DiggUtils.getIntegerParameter( strIdExpression );

        if ( ( nIdEntry != -1 ) && ( nIdExpression != -1 ) )
        {
            EntryHome.deleteVerifyBy( nIdEntry, nIdExpression, getPlugin(  ) );
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

        int nIdEntry = DiggUtils.getIntegerParameter( strIdEntry );
        int nIdExpression = DiggUtils.getIntegerParameter( strIdExpression );

        if ( ( nIdEntry != -1 ) && ( nIdExpression != -1 ) )
        {
            EntryHome.insertVerifyBy( nIdEntry, nIdExpression, getPlugin(  ) );
        }

        if ( nIdEntry != -1 )
        {
            return getJspModifyEntry( request, nIdEntry );
        }

        return getHomeUrl( request );
    }

    /**
     * get reference list of digg state
     *
     * @param locale
     *            the locale
     * @return reference list of digg state
     */
    private ReferenceList getRefListDiggState( Locale locale )
    {
        ReferenceList refListState = new ReferenceList(  );
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
        ReferenceList refList = new ReferenceList(  );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        String strYes = I18nService.getLocalizedString( PROPERTY_YES, locale );
        String strNo = I18nService.getLocalizedString( PROPERTY_NO, locale );

        refList.addItem( -1, strAll );
        refList.addItem( 1, strYes );
        refList.addItem( 0, strNo );

        return refList;
    }

    /**
     * Modify the order in the list of diggSubmit
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    public String doModifyDiggSubmitOrder( HttpServletRequest request )
    {
        int nIdDiggSubmit = Integer.parseInt( request.getParameter( PARAMETER_ID_DIGG_SUBMIT ) );

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdDiggSubmit, false, getPlugin(  ) );
        int nIdDigg = Integer.parseInt( request.getParameter( PARAMETER_ID_DIGG ) );

        if ( diggSubmit != null )
        {
            int nNewOrder = Integer.parseInt( request.getParameter( PARAMETER_DIGG_SUBMIT_ORDER ) );

            _diggSubmitService.updateDiggSubmitOrder( diggSubmit.getDiggSubmitOrder(  ), nNewOrder,
                diggSubmit.getDigg(  ).getIdDigg(  ),diggSubmit.isPinned(), getPlugin(  ) );
        }

        return getJspManageDiggSubmitOrder( request ) + "?" + PARAMETER_ID_DIGG + "=" + nIdDigg;
    }

    /**
     * Builts a list of sequence numbers
     *
     * @param nIdDigg
     *            the id of the Digg
     * @return the list of sequence numbers
     */
    private ReferenceList getDiggSubmitOrderList( int nIdDigg,boolean bListPinned )
    {
        int nMax = _diggSubmitService.getMaxOrderList( nIdDigg,bListPinned, getPlugin(  ) );
        ReferenceList list = new ReferenceList(  );

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
        Plugin plugin = getPlugin(  );
        Locale locale = getLocale(  );
        Timestamp tFirstDateFilter = null;
        Timestamp tLastDateFilter = null;
        DiggFilter diggFilter = new DiggFilter(  );
        diggFilter.setIdState( Digg.STATE_ENABLE );

        List<Digg> listDigg = DiggHome.getDiggList( diggFilter, plugin );

        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        String strFirstDateFilter = request.getParameter( PARAMETER_FIRST_DATE_FILTER );
        String strLastDateFilter = request.getParameter( PARAMETER_LAST_DATE_FILTER );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_FILTER);
    	String strIdType = request.getParameter( PARAMETER_ID_TYPE_DIGG);
    
    	int nIdCategory=DiggUtils.getIntegerParameter(strIdCategory);
    	int nIdType=DiggUtils.getIntegerParameter(strIdType);

        if ( ( strIdDigg != null ) && !strIdDigg.equals( EMPTY_STRING ) )
        {
            _nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        }
        else if ( listDigg.size(  ) > 0 )
        {
            _nIdDigg = listDigg.get( 0 ).getIdDigg(  );
        }

        Digg digg = DiggHome.findByPrimaryKey( _nIdDigg, plugin );

        if ( ( strFirstDateFilter != null ) && !strFirstDateFilter.equals( "" ) )
        {
            try
            {
                tFirstDateFilter = new Timestamp( DateUtil.formatDate( strFirstDateFilter, locale ).getTime(  ) );
            }
            catch ( Exception e )
            {
                tFirstDateFilter = null;
            }
        }

        if ( ( strLastDateFilter != null ) && !strLastDateFilter.equals( "" ) )
        {
            try
            {
                tLastDateFilter = new Timestamp( DateUtil.formatDate( strLastDateFilter, locale ).getTime(  ) );
            }
            catch ( Exception e )
            {
                tLastDateFilter = null;
            }
        }

        // build Filter
        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdDigg( _nIdDigg );
        filter.setDateFirst( tFirstDateFilter );
        filter.setDateLast( tLastDateFilter );
        filter.setIdCategory(nIdCategory);
        filter.setIdType(nIdType);

        // number of comments
        filter.setIdDiggSubmitState( DiggSubmit.STATE_PUBLISH );

        int nNbComments = _commentSubmitService.getCountCommentSubmit( filter, plugin );

        // number of votes
        filter.setIdDiggSubmitState( DiggSubmit.STATE_PUBLISH );

        int nNbVotes = 0;

        List<DiggSubmit> listDiggSubmit = _diggSubmitService.getDiggSubmitList( filter, plugin );
        List<String> listUsersKey = new ArrayList(  );

        for ( DiggSubmit d : listDiggSubmit )
        {
            nNbVotes += d.getNumberVote(  );

            if ( !listUsersKey.contains( d.getLuteceUserKey(  ) ) )
            {
                listUsersKey.add( d.getLuteceUserKey(  ) );
            }
        }

        // number of digg submit
        filter.setIdDiggSubmitState( DiggSubmit.STATE_DISABLE );

        int nNbDiggSubmitDisabled = _diggSubmitService.getCountDiggSubmit( filter, plugin );
        filter.setIdDiggSubmitState( DiggSubmit.STATE_WAITING_FOR_PUBLISH );

        int nNbDiggSubmitWaiting = _diggSubmitService.getCountDiggSubmit( filter, plugin );
        filter.setIdDiggSubmitState( DiggSubmit.STATE_PUBLISH );

        int nNbDiggSubmitPublished = _diggSubmitService.getCountDiggSubmit( filter, plugin );

        // high scores
        DiggUtils.initSubmitFilterBySort( filter, SubmitFilter.SORT_BY_SCORE_DESC );

        int nNumberMaxDiggSubmit = AppPropertiesService.getPropertyInt( PROPERTY_DIGGSUBMIT_HIGHSCORES, 10 );
        listDiggSubmit = _diggSubmitService.getDiggSubmitList( filter, plugin, nNumberMaxDiggSubmit );

        ReferenceList refDiggList = DiggUtils.getRefListDigg( listDigg, false );

        HashMap model = new HashMap(  );

        model.put( MARK_FIRST_DATE_FILTER,
            ( tFirstDateFilter == null ) ? null : new Date( tFirstDateFilter.getTime(  ) ) );
        model.put( MARK_LAST_DATE_FILTER, ( tLastDateFilter == null ) ? null : new Date( tLastDateFilter.getTime(  ) ) );
        model.put( MARK_DIGG, digg );

        if ( nNbDiggSubmitPublished != 0 )
        {
            float fV = (float) nNbVotes / nNbDiggSubmitPublished;
            float fC = (float) nNbComments / nNbDiggSubmitPublished;

            BigDecimal bd = new BigDecimal( fV );
            bd = bd.setScale( 2, BigDecimal.ROUND_HALF_UP );

            BigDecimal bd2 = new BigDecimal( fC );
            bd2 = bd2.setScale( 2, BigDecimal.ROUND_HALF_UP );

            model.put( MARK_NUMBER_VOTES, bd.toString(  ) );
            model.put( MARK_NUMBER_COMMENTS, bd2.toString(  ) );
        }
        
      	if(  digg.getCategories()!=null && !digg.getCategories().isEmpty() )
    	{
    		ReferenceList refCategoryList = DiggUtils.getRefListCategory( digg.getCategories(  ) );
    		DiggUtils.addEmptyItem(refCategoryList);
    		model.put( MARK_CATEGORY_LIST, refCategoryList );
    		model.put( MARK_DIGG_SUBMIT_CATEGORY_SELECTED, nIdCategory );
    		if(nIdCategory!=DiggUtils.CONSTANT_ID_NULL)
    		{
    			model.put( MARK_CATEGORY_FILTER, CategoryHome.findByPrimaryKey(nIdCategory, plugin));
    			
    		}
    	
    			
    	}
     	if(  digg.getDiggSubmitTypes()!=null && !digg.getDiggSubmitTypes().isEmpty() )
    	{
    		ReferenceList refDiggSubmitTypes = DiggUtils.getRefListType(digg.getDiggSubmitTypes() );
    		DiggUtils.addEmptyItem(refDiggSubmitTypes);
    		model.put( MARK_DIGG_SUBMIT_TYPE_LIST, refDiggSubmitTypes );
    		model.put( MARK_DIGG_SUBMIT_TYPE_SELECTED,nIdType);
    		if(nIdType!=DiggUtils.CONSTANT_ID_NULL)
    		{
    			model.put( MARK_TYPE_FILTER, DiggSubmitTypeHome.findByPrimaryKey(nIdType, plugin));
    			
    		}
    	}
       
       

        model.put( MARK_NUMBER_DIGGSUBMIT_DISABLED, nNbDiggSubmitDisabled );
        model.put( MARK_NUMBER_DIGGSUBMIT_WAITING, nNbDiggSubmitWaiting );
        model.put( MARK_NUMBER_DIGGSUBMIT_PUBLISHED, nNbDiggSubmitPublished );
        model.put( MARK_NUMBER_USERS, listUsersKey.size(  ) );
        model.put( MARK_HIGH_SCORES, listDiggSubmit );
        model.put( MARK_DIGG_LIST, refDiggList );
        model.put( MARK_URL, AppPathService.getBaseUrl( request ) + JSP_MANAGE_COMMENT_SUBMIT + "?id_digg_submit=" );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_STATISTICS_DIGG, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    

    /**
     * Gets the confirmation page of update all digg submit
     *
     * @param request
     *            The HTTP request
     * @return the confirmation page of update all digg submit
     */
    public String getConfirmUpdateAllDisplayOfDiggSubmit( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        String strMessage;
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        if ( ( nIdDigg == -1 ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_UPDATE_ALL_DIGG_SUBMIT, getUser(  ) ) )
        {
            return getJspManageDigg( request );
        }

        UrlItem url = new UrlItem( JSP_DO_UPDATE_ALL_DIGG_SUBMIT );
        url.addParameter( PARAMETER_ID_DIGG, strIdDigg );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_UPDATE_ALL_DIGG_SUBMIT, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Do update all digg submit
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doUpdateAllDisplayOfDiggSubmit( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        if ( RBACService.isAuthorized( Digg.RESOURCE_TYPE, EMPTY_STRING + nIdDigg,
                    DigglikeResourceIdService.PERMISSION_UPDATE_ALL_DIGG_SUBMIT, getUser(  ) ) )
        {
            DigglikeService.getInstance(  ).updateAllDisplayOfDiggSubmit( nIdDigg, plugin, getLocale(  ) );
        }

        return getJspManageDigg( request );
    }

    /**
     * do mofify advanced parameters
     * @param request the request
     * @return url
     */
    public String doModifyAdvancedParameters( HttpServletRequest request )
    {
        Digg digg;
        Plugin plugin = getPlugin(  );
        String strIdDefaultDigg = request.getParameter( PARAMETER_ID_DEFAULT_DIGG );
        int nIdDefaultDigg = DiggUtils.getIntegerParameter( strIdDefaultDigg );

        if ( !RBACService.isAuthorized( Digg.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DigglikeResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            return getManageDigg( request );
        }

        //find default Digg
        DiggFilter filter = new DiggFilter(  );
        filter.setIdDefaultDigg( DiggFilter.ID_TRUE );

        List<Digg> listDefaultDigg = DiggHome.getDiggList( filter, plugin );

        //update default digg
        for ( Digg defaultDigg : listDefaultDigg )
        {
            digg = DiggHome.findByPrimaryKey( defaultDigg.getIdDigg(  ), plugin );
            digg.setDefaultDigg( false );
            DiggHome.update( digg, plugin );
        }

        if ( nIdDefaultDigg != DiggUtils.CONSTANT_ID_NULL )
        {
            Digg diggDefault = DiggHome.findByPrimaryKey( nIdDefaultDigg, plugin );

            if ( diggDefault != null )
            {
                diggDefault.setDefaultDigg( true );
                DiggHome.update( diggDefault, plugin );
            }
        }

        return getJspManageAdvancedParameters( request );
    }


    
    
    private void  updateSearchFieldsData(HttpServletRequest request)
    {
    	
    	String strIdDigg = request.getParameter( PARAMETER_ID_DIGG);
    	String strIdDiggSumitState = request.getParameter( PARAMETER_ID_DIGG_SUBMIT_STATE );
        String strIdDiggSubmitSort = request.getParameter( PARAMETER_ID_DIGG_SUBMIT_SORT );
        String strIdDiggSubmitReport = request.getParameter( PARAMETER_ID_DIGG_SUBMIT_REPORT );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_FILTER);
    	String strIdType = request.getParameter( PARAMETER_ID_TYPE_DIGG);
    
        String strQuery = request.getParameter( PARAMETER_QUERY );
    	
        if ( ( strIdDigg != null ) && !strIdDigg.equals( EMPTY_STRING ) )
        {
            getSearchFields().setIdDigg(DiggUtils.getIntegerParameter( strIdDigg ));
        }
    	if ( ( strIdDiggSubmitSort != null ) && !strIdDiggSubmitSort.equals( EMPTY_STRING ) )
        {
            getSearchFields().setIdDiggSubmitSort(DiggUtils.getIntegerParameter( strIdDiggSubmitSort ));
        }

        if ( ( strIdDiggSubmitReport != null ) && !strIdDiggSubmitReport.equals( EMPTY_STRING ) )
        {
        	getSearchFields().setIdDiggSubmitReport(DiggUtils.getIntegerParameter( strIdDiggSubmitReport ));
        }
        if ( ( strIdDiggSumitState != null ) && !strIdDiggSumitState.equals( EMPTY_STRING ) )
        {
        	getSearchFields().setIdDiggSumitState(DiggUtils.getIntegerParameter( strIdDiggSumitState ));
        }
        if ( ( strIdCategory != null ) && !strIdCategory.equals( EMPTY_STRING ) )
        {
        	getSearchFields().setIdCategory(DiggUtils.getIntegerParameter( strIdCategory ));
        }
        if ( ( strIdType != null ) && !strIdType.equals( EMPTY_STRING ) )
        {
        	getSearchFields().setIdType(DiggUtils.getIntegerParameter( strIdType ));
        }

        if ( strQuery != null )
        {
        	getSearchFields().setQuery(strQuery);
        }
    }
    
    private DigglikeAdminSearchFields getSearchFields()
    {
    	
    	return _searchFields;
    	
    }
    
    
}
