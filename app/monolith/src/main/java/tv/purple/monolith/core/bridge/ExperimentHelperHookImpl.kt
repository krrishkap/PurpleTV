package tv.purple.monolith.core.bridge

import io.reactivex.Observable
import tv.purple.monolith.bridge.ex.IExperimentHelper
import tv.purple.monolith.core.LoggerImpl
import tv.twitch.android.provider.experiments.ChannelExperiment
import tv.twitch.android.provider.experiments.Experiment
import tv.twitch.android.provider.experiments.ExperimentHelper
import tv.twitch.android.provider.experiments.MiniExperimentModel
import tv.twitch.android.provider.experiments.RemoteConfigurable

class ExperimentHelperHookImpl(
    private val org: IExperimentHelper
) : ExperimentHelper {
    companion object {
        private fun hookExperiment(p0: Experiment): Boolean? {
            return when (p0) {
                Experiment.AVAILABILITY_TRACKING,
                Experiment.NETWORK_REQUEST_TRACKING,
                Experiment.THEATRE_MUTE_BUTTON,
                Experiment.VISAGE_TRACKING -> false

                Experiment.MESSAGE_EFFECT_RENDERING,
                Experiment.ANIMATED_EMOTES,
                Experiment.PROFILE_SETTINGS_REORG,
                Experiment.CHAT_SETTINGS_MVVM, -> true

                else -> null
            }
        }
    }

    override fun isInGroupForMultiVariantExperiment(
        p0: Experiment,
        p1: String
    ): Boolean {
        return hookExperiment(p0) ?: org.isInGroupForMultiVariantExperimentOrg(
            p0,
            p1
        ).also { res ->
            LoggerImpl.debug { "|->isInGroupForMultiVariantExperiment($p0, $p1) --> $res" }
        }
    }

    override fun isInOnGroupForBinaryExperiment(
        p0: Experiment
    ): Boolean {
        return hookExperiment(p0) ?: org.isInOnGroupForBinaryExperimentOrg(p0).also { res ->
            LoggerImpl.debug { "|->isInOnGroupForBinaryExperiment($p0) --> $res" }
        }
    }

    override fun isInRestrictedLocaleForExperiment(
        p0: Experiment
    ): Boolean {
        return org.isInRestrictedLocaleForExperimentOrg(p0).also { res ->
            LoggerImpl.debug { "|->isInRestrictedLocaleForExperiment($p0) --> $res" }
        }
    }

    override fun overrideGroupForExperiment(
        p0: RemoteConfigurable?, p1: String?
    ) {
        org.overrideGroupForExperimentOrg(p0, p1).also {
            LoggerImpl.debug { "|->overrideGroupForExperiment($p0, $p1)" }
        }
    }

    override fun getGroupForChannelExperiment(
        p0: ChannelExperiment?,
        p1: String?
    ): String? {
        return org.getGroupForChannelExperimentOrg(p0, p1).also {
            LoggerImpl.debug { "|->getGroupForChannelExperiment($p0, $p1)" }
        }
    }

    override fun getGroupForExperiment(
        p0: Experiment,
        p1: String?
    ): String? {
        return when (p0) {
            else -> org.getGroupForExperimentOrg(p0, p1)
        }.also { res ->
            LoggerImpl.debug { "|->getGroupForExperiment($p0, $p1) --> $res" }
        }
    }

    override fun getModelForExperimentId(
        p0: String
    ): MiniExperimentModel? {
        return org.getModelForExperimentIdOrg(p0).also { res ->
            LoggerImpl.debug { "|->getModelForExperimentId($p0) --> $res" }
        }
    }

    override fun getTreatmentForExperimentId(
        p0: String?,
        p1: String?
    ): String? {
        return org.getTreatmentForExperimentIdOrg(p0, p1).also { res ->
            LoggerImpl.debug { "|->getTreatmentForExperimentId($p0, $p1) --> $res" }
        }
    }

    override fun getUpdatedRemoteConfigurablesObservable(): Observable<Set<RemoteConfigurable?>?>? {
        return org.getUpdatedRemoteConfigurablesObservableOrg()?.doOnNext { set ->
            set?.filterNotNull()?.forEach { config ->
                LoggerImpl.debug { "|->getUpdatedRemoteConfigurablesObservable() --> $config" }
            }
        }
    }

    override fun isInDefaultControlGroup(
        p0: Experiment
    ): Boolean {
        return org.isInDefaultControlGroupOrg(p0).also { res ->
            LoggerImpl.debug { "|->isInDefaultControlGroup($p0) --> $res" }
        }
    }

    override fun refreshExperiments(
        p0: Int
    ) {
        org.refreshExperimentsOrg(p0).also {
            LoggerImpl.debug { "|->refreshExperiments($p0)" }
        }
    }

    override fun refreshIfNeeded(
        p0: Int
    ) {
        org.refreshIfNeededOrg(p0).also {
            LoggerImpl.debug { "|->refreshIfNeeded($p0)" }
        }
    }

    override fun updateEnabledGroupsForActiveExperiments(): Set<RemoteConfigurable?>? {
        return org.updateEnabledGroupsForActiveExperimentsOrg().also { res ->
            res?.filterNotNull()?.forEach { config ->
                LoggerImpl.debug { "|->updateEnabledGroupsForActiveExperiments() --> $config" }
            }
        }
    }
}